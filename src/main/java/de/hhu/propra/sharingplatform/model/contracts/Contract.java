package de.hhu.propra.sharingplatform.model.contracts;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import de.hhu.propra.sharingplatform.model.*;
import de.hhu.propra.sharingplatform.model.payments.Payment;
import de.hhu.propra.sharingplatform.service.payment.IPaymentApi;
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

    public void pay(IPaymentApi paymentApi){}

    public void prepare(IPaymentApi paymentApi){}
}
