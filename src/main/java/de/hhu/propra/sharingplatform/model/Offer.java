package de.hhu.propra.sharingplatform.model;

import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

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

    public Offer(Date start, Date end) {
        this.setStart(start);
        this.setEnd(end);

        this.accept = false;
        this.decline = false;
    }
}
