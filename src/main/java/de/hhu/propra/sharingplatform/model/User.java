package de.hhu.propra.sharingplatform.model;


import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Data;

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
    private Integer rating;
    private boolean ban;
    private boolean deleted;
    private String passwordHash;
    private String salt;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST,
        CascadeType.REFRESH}, mappedBy = "borrower")
    private List<Contract> contracts;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST,
        CascadeType.REFRESH}, mappedBy = "owner")
    private List<Item> items;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST,
        CascadeType.REFRESH}, mappedBy = "borrower")
    private List<Offer> offers;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST,
        CascadeType.REFRESH}, mappedBy = "sender")
    private List<Payment> paymentsSend;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST,
        CascadeType.REFRESH}, mappedBy = "recipient")
    private List<Payment> paymentsReceive;

    public User() {
        contracts = new ArrayList<>();
        items = new ArrayList<>();
        offers = new ArrayList<>();
        paymentsSend = new ArrayList<>();
        paymentsReceive = new ArrayList<>();
    }

    public void setPassword(String password){
        String pepper = "";
        salt = UUID.randomUUID().toString();
        password += salt;
        password += pepper;
        passwordHash = Hashing.sha512().hashString(password, StandardCharsets.UTF_8).toString();
    }

    public boolean checkPassword(String password){
        String pepper = "";
        /*Properties properties = new Properties();
        try (InputStream is = getClass().getResourceAsStream("application.properties")) {
            properties.load(is);
            pepper = properties.getProperty("passwords.pepper");
        } catch (IOException ex) {
            //TODO
        }*/
        password += salt;
        password += pepper;
        return passwordHash.equals(Hashing.sha512().hashString(password, StandardCharsets.UTF_8).toString());
    }
}
