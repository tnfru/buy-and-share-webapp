package de.hhu.propra.sharingplatform.model.payments;

import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.payment.IPaymentApi;
import javax.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
public class BorrowPayment extends Payment {

    long amountProPayId;
    int bail;
    long bailProPayId;

    @Deprecated
    public BorrowPayment(User sender, User recipient, int amount, int bail) {
        super(amount, sender.getPropayId(), recipient.getPropayId());
        this.proPayIdSender = sender.getPropayId();
        this.proPayIdRecipient = recipient.getPropayId();
        this.bail = bail;
    }

    public BorrowPayment(String sender, String recipient, int amount, int bail) {
        super(amount, sender, recipient);
        this.proPayIdSender = sender;
        this.proPayIdRecipient = recipient;
        this.bail = bail;
    }

    private BorrowPayment() {

    }

    /**
     * Reserve Money and bail.
     *
     * @param paymentApi api for payment.
     */

    public void reserve(IPaymentApi paymentApi) {
        bailProPayId = paymentApi.reserveMoney(proPayIdSender, proPayIdRecipient, bail);
        amountProPayId = paymentApi.reserveMoney(proPayIdSender, proPayIdRecipient, super.amount);
    }

    /**
     * Frees the reserved money for payment, pays the recipient. Does not free the bail!
     *
     * @param paymentApi api for payment.
     */
    @Override
    public void pay(IPaymentApi paymentApi) {
        freeCharge(paymentApi);
        paymentApi.transferMoney(super.amount, proPayIdSender, proPayIdRecipient);
    }

    public void punishBail(IPaymentApi paymentApi) {
        paymentApi.punishReservation(bailProPayId, proPayIdSender);
    }

    public void freeBail(IPaymentApi paymentApi) {
        paymentApi.freeReservation(bailProPayId, proPayIdSender);
    }

    public void freeCharge(IPaymentApi paymentApi) {
        paymentApi.freeReservation(amountProPayId, proPayIdSender);
    }
}
