package de.hhu.propra.sharingplatform.model;


import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@Data
@Entity
public class User {

    @Autowired
    private Environment env;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String address;
    private String email;
    private String propayId;
    private int rating;
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

    public void setPassword(String password){
        salt = UUID.randomUUID().toString();
        password += salt;
        password += env.getProperty("passwords.pepper");
    }

    public boolean checkPassword(String password){

    }
}
