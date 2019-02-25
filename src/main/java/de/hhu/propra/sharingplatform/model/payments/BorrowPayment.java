package de.hhu.propra.sharingplatform.model.payments;

import de.hhu.propra.sharingplatform.model.User;

public class BorrowPayment {

    long amountProPayId;
    int bail;
    long bailProPayId;

    public BorrowPayment(User sender, User recipient, int amount, int bail) {
        this.proPayIdSender = sender.getPropayId();
        this.proPayIdRecipient = recipient.getPropayId();
        this.amount = amount;
        this.bail = bail;
    }
}
