package de.hhu.propra.sharingplatform.model.payments;

import de.hhu.propra.sharingplatform.model.User;
import de.hhu.propra.sharingplatform.service.payment.IPaymentApi;

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
     * Frees the reserved money for payment, pays the recipient.
     * Does not free the bail!
     *
     * @param paymentApi api for payment.
     */
    @Override
    public void pay(IPaymentApi paymentApi) {
        paymentApi.freeReservation(amountProPayId, proPayIdSender);
        paymentApi.transferMoney(super.amount, proPayIdSender, proPayIdRecipient);
    }
}
