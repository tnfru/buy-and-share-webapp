package de.hhu.propra.sharingplatform.service.payment;

public interface IPaymentApi {

    /**
     * Reserve money on the account.
     *
     * @param fromAccount account from which the money is reserved.
     * @param toAccount   account to which the money will be send
     *                    once the reservation is punished.
     * @param amount      amount of money reserved.
     * @return the ID of the reservation. Use this ID for free/punishReservation().
     */
    long reserveMoney(String fromAccount, String toAccount, int amount);

    /**
     * Free the reservation done by reserveMoney().
     *
     * @param reservationId ID as it is returned by reserveMoney().
     * @param accountSender accountID od the sender.
     */
    void freeReservation(long reservationId, String accountSender);

    /**
     * Punish the reservation done by reserveMoney(). Money is transferred.
     *
     * @param reservationId ID as it is returned by reserveMoney().
     * @param accountSender accountID od the sender.
     */
    void punishReservation(long reservationId, String accountSender);

    /**
     * Get the amount of all money on the account. Reserved money is still included here.
     *
     * @param account accountId.
     * @return account balance.
     */
    int getAccountBalance(String account);

    /**
     * Get the sum of all money reserved on the account.
     *
     * @param account accountId.
     * @return money reserved.
     */
    int getAccountReservations(String account);

    /**
     * Get the amount of all money on the account the user has access to. Reservations
     * are not included here.
     *
     * @param account accountId.
     * @return account balance.
     */
    int getAccountBalanceLiquid(String account);

    /**
     * Add money to the account. Money comes from nothing...
     * (Mostly used for test purposes).
     *
     * @param account accountId.
     * @param amount  amount of money.
     */
    void addMoney(String account, int amount);

    /**
     * Transfer money from one account to another.
     *
     * @param amount      amount transferred.
     * @param fromAccount account sender.
     * @param toAccount   account recipiant.
     */
    void transferMoney(int amount, String fromAccount, String toAccount);
}
