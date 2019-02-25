package de.hhu.propra.sharingplatform.service.payment;

import de.hhu.propra.sharingplatform.model.Contract;
import de.hhu.propra.sharingplatform.model.Item;
import de.hhu.propra.sharingplatform.model.Payment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public interface IPaymentService {

    /**
     * Creates the payment information from a contract.
     *
     * @param contract contract for payment.
     * @return The payment information.
     */
    Payment createPayment(Contract contract);


    /**
     * Calculates the total price by calculating the days between start and end
     * and multiplying it by the price per day of the Item.
     *
     * @param item  Item including price per day.
     * @param start Date of start.
     * @param end   Date of end.
     * @return price in euro.
     */
    int calculateTotalPrice(Item item, LocalDateTime start, LocalDateTime end);


    /**
     * Checks if the borrower in the contract is able to pay.
     * This is done by getting the account balance and subtracting
     * all open Bails.
     *
     * @param contract Contract
     * @return true if enough money is available.
     */
    boolean recipientSolvent(Contract contract);

    /**
     * Transfers the money in the contract.
     *
     * @param contract contract
     */
    void transferPayment(Contract contract);

    /**
     * Frees the reservation of the bail.
     * Is called when a contract ends and Item owner accepts the return.
     *
     * @param contract contract
     */
    void freeBailReservation(Contract contract);

    /**
     * Transfers the bail to the item owner.
     *
     * @param contract
     */
    void punishBailReservation(Contract contract);
}
