package de.hhu.propra.sharingplatform.model.payments;

import de.hhu.propra.sharingplatform.model.contracts.Contract;
import de.hhu.propra.sharingplatform.service.payment.IPaymentApi;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@ToString(exclude = "contract")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    int amount;
    String proPayIdSender;
    String proPayIdRecipient;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH},
        mappedBy = "payment")
    Contract contract;

    @SuppressWarnings("unused")
    public Payment() {
        // used for jpa
    }

    public Payment(int amount, String from, String to) {
        this.amount = amount;
        this.proPayIdSender = from;
        this.proPayIdRecipient = to;
    }


    public void pay(IPaymentApi paymentApi) {
        paymentApi.transferMoney(amount, proPayIdSender, proPayIdRecipient);
    }
}
