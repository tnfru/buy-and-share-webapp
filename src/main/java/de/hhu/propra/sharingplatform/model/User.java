package de.hhu.propra.sharingplatform.model;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Transient;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

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
    private boolean ban;
    private boolean deleted;
    private String passwordHash;
    private int positiveRating;
    private int negativeRating;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST,
        CascadeType.REFRESH}, mappedBy = "borrower")
    private List<Contract> contracts;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST,
        CascadeType.REFRESH}, mappedBy = "owner")
    private List<Item> items;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST,
        CascadeType.REFRESH}, mappedBy = "borrower")
    private List<Offer> offers;

    @Transient
    @Value("${passwords.pepper}")
    private String pepper;

    public User() {
        contracts = new ArrayList<>();
        items = new ArrayList<>();
        offers = new ArrayList<>();
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
            return String.format(format, rating).replace(',', '.');
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
}
