package de.hhu.propra.sharingplatform.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private User wantsToBorrow;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Item item;


    public Contract() {
    }

    public Contract(Offer offer) {
        //TODO offer can be null
        this.wantsToBorrow = offer.getWantsToBorrow();
        this.item = offer.getItem();
    }
}
