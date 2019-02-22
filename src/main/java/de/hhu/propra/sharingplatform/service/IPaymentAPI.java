package de.hhu.propra.sharingplatform.service;

import de.hhu.propra.sharingplatform.model.Payment;
import de.hhu.propra.sharingplatform.model.User;
import org.springframework.stereotype.Component;

@Component
public interface IPaymentAPI {
    void enforcePayment(Payment payment, int totalPrice);
    void createAccount(String proPayId, int amount);
    void addMoney(String proPayId, int amount);
    boolean isSolvent(User borrower, int amountOwed);
    void freeReservation(long amountProPayId, String proPayIdSender);
    void transferMoney(Payment paymentInfo);
    void punishReservation(long bailProPayId, String proPayIdSender);
    int getAccountBalance(String proPayID);
}
