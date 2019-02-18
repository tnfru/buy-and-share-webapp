package de.hhu.propra.sharingplatform.model;

import java.util.Date;
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
@ToString(exclude = {"item", "borrower"})
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Item item;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private User borrower;

    private Date start;
    private Date end;
    private boolean accept;
    private boolean decline;

    public Offer() {
    }

    public Offer(Item item, User borrower, Date start, Date end) {
        this.setStart(start);
        this.setEnd(end);

        this.setItem(item);
        this.setBorrower(borrower);

        this.accept = false;
        this.decline = false;
    }
}
