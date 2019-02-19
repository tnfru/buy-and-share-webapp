package de.hhu.propra.sharingplatform.model;

import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private User borrower;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Payment payment;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Item item;
    private Date start;
    private Date expectedEnd;
    private Date realEnd;
    private boolean isConflict;

    @SuppressWarnings("unused")
    private Contract() {
        // used by jpa
    }

    public Contract(Offer offer) {
        this.borrower = offer.getBorrower();
        this.item = offer.getItem();
        this.expectedEnd = offer.getEnd();
        this.realEnd = offer.getEnd();
        this.start = offer.getStart();
    }
}
