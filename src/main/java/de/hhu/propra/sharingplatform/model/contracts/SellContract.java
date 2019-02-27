package de.hhu.propra.sharingplatform.model.contracts;

import de.hhu.propra.sharingplatform.model.items.ItemSale;
import de.hhu.propra.sharingplatform.model.payments.Payment;
import javax.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
public class SellContract extends Contract {

    private SellContract() {
    }

    public SellContract(ItemSale item, String fromPropaiId, String toPropayId) {
        super.item = item;
        //TODO: item sale price..
        int amount = item.getPrice();
        super.payment = new Payment(0, fromPropaiId, toPropayId);
    }

}
