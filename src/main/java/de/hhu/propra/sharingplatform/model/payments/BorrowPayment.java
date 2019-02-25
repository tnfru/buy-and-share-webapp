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
        super(amount, sender.getPropayId(), recipient.getPropayId());
        this.proPayIdSender = sender.getPropayId();
        this.proPayIdRecipient = recipient.getPropayId();
        this.bail = bail;
    }

    public void reserve(IPaymentApi paymentApi){
        bailProPayId = paymentApi.reserveMoney(proPayIdSender, proPayIdRecipient, bail);
        amountProPayId = paymentApi.reserveMoney(proPayIdSender, proPayIdRecipient, super.amount);
    }

    @Override
    public void pay(IPaymentApi paymentApi){
        paymentApi.freeReservation(bailProPayId, proPayIdSender);
        paymentApi.freeReservation(amountProPayId, proPayIdSender);
        paymentApi.transferMoney(super.amount, proPayIdSender, proPayIdRecipient);
    }
}
