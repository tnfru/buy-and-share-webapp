package de.hhu.propra.sharingplatform.service.payment;

import de.hhu.propra.sharingplatform.model.contracts.BorrowContract;
import de.hhu.propra.sharingplatform.model.contracts.Contract;
import de.hhu.propra.sharingplatform.model.contracts.SellContract;

public interface IPaymentService {

    /**
     * Creates the payment information from a contract.
     *
     * @param contract contract for payment.
     */
    void createPayment(Contract contract);

    /**
     * Transfers the money in the contract.
     *
     * @param contract contract
     */
    void transferPayment(BorrowContract contract);

    void transferPayment(SellContract contract);

    /**
     * Frees the reservation of the bail.
     * Is called when a contract ends and ItemRental owner accepts the return.
     *
     * @param contract contract
     */
    void freeBailReservation(BorrowContract contract);

    /**
     * Transfers the bail to the itemRental owner.
     *
     * @param contract contract
     */
    void punishBailReservation(BorrowContract contract);

    void freeChargeReservation(BorrowContract contract);
}
