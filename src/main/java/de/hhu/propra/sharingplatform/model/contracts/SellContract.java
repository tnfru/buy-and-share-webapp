package de.hhu.propra.sharingplatform.model.contracts;

import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.model.items.ItemSale;
import de.hhu.propra.sharingplatform.model.payments.Payment;
import de.hhu.propra.sharingplatform.service.payment.IPaymentApi;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
public class SellContract extends Contract {

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private User customer;

    @SuppressWarnings("unused")
    public SellContract() {
        // used by JPA
    }

    public SellContract(ItemSale item, User customer) {
        super.item = item;
        this.customer = customer;
        super.payment = new Payment(item.getPrice(), customer.getPropayId(),
            item.getOwner().getPropayId());
    }

    public boolean isBalanced(IPaymentApi apiService) {
        return payment.isBalanced(apiService);
    }
}
