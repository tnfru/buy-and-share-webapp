package de.hhu.propra.sharingplatform.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.Data;


@Entity
@Data
public class ItemSale extends Item {

    private Integer price;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST,
        CascadeType.REFRESH}, mappedBy = "itemRental")
    private List<Contract> contracts = new ArrayList<>();

    @SuppressWarnings("unused")
    private ItemSale() {
        // used for jpa
    }

    public ItemSale(User owner) {
        super(owner);
    }
}
