package de.hhu.propra.sharingplatform.model;

import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@ToString(exclude = {"itemRental", "borrower"})
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private ItemRental itemRental;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private User borrower;

    private LocalDateTime start;
    private LocalDateTime end;
    private boolean accept;
    private boolean decline;

    @SuppressWarnings("unused")
    private Offer() {
        // used for jpa
    }

    public Offer(ItemRental itemRental, User borrower, LocalDateTime start, LocalDateTime end) {
        this.setStart(start);
        this.setEnd(end);

        this.setItemRental(itemRental);
        this.setBorrower(borrower);

        this.accept = false;
        this.decline = false;
    }
}
