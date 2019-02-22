package de.hhu.propra.sharingplatform.service.Payment;

import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.Payment;
import de.hhu.propra.sharingplatform.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public interface IPaymentService {

    Payment createPayment(Contract contract);

    void createPayment(User sender, User recipient, int amount, int bail);

    int calculateTotalPrice(Item item, LocalDateTime start, LocalDateTime end);

    boolean recipientSolvent(Contract contract);

    void transferPayment(Contract contract);

    void freeBailReservation(Contract contract);

    void punishBailReservation(Contract contract);
}
