package de.hhu.propra.sharingplatform.model;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String description;
    private int deposit;
    private int price; // each day
    private boolean available;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private User owner;
    private String location; // maybe change to java location class
    private boolean deleted;
}
