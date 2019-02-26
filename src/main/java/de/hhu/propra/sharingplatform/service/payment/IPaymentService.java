package de.hhu.propra.sharingplatform.service.payment;

import de.hhu.propra.sharingplatform.model.contracts.Contract;
import de.hhu.propra.sharingplatform.model.payments.Payment;
import org.springframework.stereotype.Component;

@Deprecated
@Component
public interface IPaymentService {

    /**
     * Creates the payment information from a contract.
     *
     * @param contract contract for payment.
     * @return The payment information.
     */
    void createPayment(Contract contract);

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
     * @param contract contract
     */
    void punishBailReservation(Contract contract);
}
