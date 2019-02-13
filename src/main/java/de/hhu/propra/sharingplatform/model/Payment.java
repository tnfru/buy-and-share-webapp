package de.hhu.propra.sharingplatform.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Payment {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private User sender;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private User recipient;
    private long amount;

    public Payment(User from, User to, long amount) {
        this.sender = from;
        this.recipient = to;
        this.amount = amount;
    }

}
