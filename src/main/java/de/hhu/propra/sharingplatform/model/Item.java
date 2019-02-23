package de.hhu.propra.sharingplatform.model;

import com.google.common.io.Files;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@ToString(exclude = "owner")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String imageFileName;

    @Column(length = 2000)
    private String description;
    private Integer bail;
    private Integer price; // each day
    private String location; // maybe change to java location class
    private boolean deleted;

    @Transient
    private MultipartFile image;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private User owner;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST,
        CascadeType.REFRESH}, mappedBy = "item")
    private List<Offer> offers = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST,
        CascadeType.REFRESH}, mappedBy = "item")
    private List<Contract> contracts = new ArrayList<>();

    @SuppressWarnings("unused")
    private Item() {
        // used for jpa
    }

    public Item(User owner) {
        this.owner = owner;
    }

    public long getActiveOffers() {
        long counter = 0;
        for (Offer offer : offers) {
            if (!(offer.isAccept() || offer.isDecline())) {
                counter++;
            }
        }
        return counter;
    }

    public List<Contract> getChosenContracts(boolean finished) {
        List<Contract> chosenContracts = new ArrayList<>();
        for (Contract contract : contracts) {
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
