package de.hhu.propra.sharingplatform.model;


import com.google.common.io.Files;
import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import de.hhu.propra.sharingplatform.model.contracts.Contract;
import de.hhu.propra.sharingplatform.model.items.Item;
import de.hhu.propra.sharingplatform.model.items.ItemRental;
import de.hhu.propra.sharingplatform.model.items.ItemSale;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String accountName;
    private String address;
    private String email;
    private String propayId;
    private String role = "user";
    private boolean ban;
    private boolean deleted;
    private String passwordHash;
    private int positiveRating;
    private int negativeRating;
    private String imageFileName = "dummy.png";

    @Transient
    private MultipartFile image;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST,
        CascadeType.REFRESH}, mappedBy = "borrower")
    private List<BorrowContract> contracts = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST,
        CascadeType.REFRESH}, mappedBy = "owner")
    private List<ItemRental> itemRentals = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST,
        CascadeType.REFRESH}, mappedBy = "owner")
    private List<ItemSale> itemSales = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST,
        CascadeType.REFRESH}, mappedBy = "borrower")
    private List<Offer> offers = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
        mappedBy = "requester")
    private List<Conflict> conflicts;

    @Transient
    @Value("${passwords.pepper}")
    private String pepper;

    public User() {
        // used by jpa
    }

    // ToDo remove setPassword method (only used by Faker)
    public void setPassword(String password) {
        passwordHash = hashPassword(password);
    }

    private String hashPassword(String plainPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(plainPassword);
    }

    public String getRating() {
        String format = "%.1f%%";
        float sum = (float) positiveRating + (float) negativeRating;
        if (sum > 0) {
            float rating = positiveRating / sum;
            rating *= 100;
            return String.format(Locale.ROOT, format, rating);
        }
        return "0.0%";
    }

    public int totalRatings() {
        return positiveRating + negativeRating;
    }

    void addPositiveRating() {
        positiveRating++;
    }

    void addNegativeRating() {
        negativeRating++;
    }

    /**
     * @return all itemRentals the user has. Items that are marked as removed are not returned
     */

    public Collection<Item> getNotRemovedItems(List<Item> list) {
        ArrayList<Item> itemsActive = new ArrayList<>();
        for (Item item : list) {
            if (!item.isDeleted()) {
                itemsActive.add(item);
            }
        }
        return itemsActive;
    }

    public List<BorrowContract> getChosenContracts(boolean finished) {
        List<BorrowContract> chosenContracts = new ArrayList<>();
        for (BorrowContract contract : contracts) {
            if (contract.isFinished() == finished) {
                chosenContracts.add(contract);
            }
        }
        return chosenContracts;
    }

    public String getImageExtension() {
        return Files.getFileExtension(image.getOriginalFilename());
    }
}
