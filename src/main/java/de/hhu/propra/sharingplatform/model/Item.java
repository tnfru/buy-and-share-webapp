package de.hhu.propra.sharingplatform.model;


import lombok.Data;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
    private String description;
    private Double bail;
    private Double price; // each day
    private boolean available;
    private String location; // maybe change to java location class
    private boolean deleted;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private User owner;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST,
        CascadeType.REFRESH}, mappedBy = "item")
    private List<Offer> offers;

    public Item() {
        offers = new ArrayList<>();
    }

    public Item(User owner) {
        this.owner = owner;
    }
}
