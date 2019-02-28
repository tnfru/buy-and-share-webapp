package de.hhu.propra.sharingplatform.model.contracts;

import de.hhu.propra.sharingplatform.model.items.Item;
import de.hhu.propra.sharingplatform.model.payments.Payment;
import de.hhu.propra.sharingplatform.service.payment.IPaymentApi;
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
public abstract class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    Payment payment;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    Item item;

    private boolean finished;

    /**
     * Pays the itemowner. Called when contract should end and be paid.
     *
     * @param paymentApi api for payment.
     */
    public void pay(IPaymentApi paymentApi) {
        payment.pay(paymentApi);
    }

    /**
     * Prepare the Contract payment. Check money available, reserve money...
     *
     * @param paymentApi api for payment.
     */
    public void prepare(IPaymentApi paymentApi) {
    }
}
