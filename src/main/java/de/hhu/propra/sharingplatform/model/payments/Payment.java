package de.hhu.propra.sharingplatform.model.payments;

import de.hhu.propra.sharingplatform.model.contracts.Contract;
import de.hhu.propra.sharingplatform.service.payment.IPaymentApi;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.Data;
import lombok.ToString;

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
    Payment() {
        // used for jpa
    }

    public Payment(int amount, String from, String to) {
        this.amount = amount;
        this.proPayIdSender = from;
        this.proPayIdRecipient = to;
    }


    public void pay(IPaymentApi paymentApi) {
        if (paymentApi.getAccountBalanceLiquid(proPayIdSender) >= amount) {
            paymentApi.transferMoney(amount, proPayIdSender, proPayIdRecipient);
        }
        //TODO: ELSE: throw exception
    }

    public boolean isBalanced(IPaymentApi paymentApi) {
        return paymentApi.getAccountBalance(proPayIdSender) >= amount;
    }
}
