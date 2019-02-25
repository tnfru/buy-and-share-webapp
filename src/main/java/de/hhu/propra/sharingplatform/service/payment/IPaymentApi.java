package de.hhu.propra.sharingplatform.service.payment;

import de.hhu.propra.sharingplatform.model.Payment;
import de.hhu.propra.sharingplatform.model.User;
import org.springframework.stereotype.Component;

@Component
public interface IPaymentApi {

    long reserveMoney(String fromAccount, String toAccount, int amount);

    void freeReservation(long reservationID, String accountSender);

    void punishReservation(long reservation, String accountSender);

    int getAccountBalance(String account);

    void addMoney(String account, int amount);

    void transferMoney(int amount, String fromAccount, String toAccount);

    int getAccountReservations(String account);
}
