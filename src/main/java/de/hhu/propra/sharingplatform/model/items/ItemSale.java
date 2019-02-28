package de.hhu.propra.sharingplatform.model.items;

import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.contracts.SellContract;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@Entity
@EqualsAndHashCode(callSuper = false)
public class ItemSale extends Item {

    private Integer price;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST,
        CascadeType.REFRESH}, mappedBy = "item")
    private List<SellContract> contracts = new ArrayList<>();

    @SuppressWarnings("unused")
    public ItemSale() {
        // used for jpa
    }

    public ItemSale(User owner) {
        super(owner);
    }
}
