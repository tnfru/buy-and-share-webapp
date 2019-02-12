package de.hhu.propra.sharingplatform.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private User wantsToBorrow;
    private Item item;


    public Contract() {
    }

    public Contract(Offer offer) {
        this.wantsToBorrow = offer.getWantsToBorrow();
        this.item = offer.getItem();
    }
}
