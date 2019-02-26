package de.hhu.propra.sharingplatform.service.payment;

import org.springframework.stereotype.Component;

public interface IBankAccountService {
    /**
     * Get the total available Money on the account.
     *
     * @param accountname accountname.
     * @return money available.
     */
    int getAccountBalance(String accountname);

    /**
     * Transfer money out of nowhere to the account.
     *
     * @param amount    amount of money.
     * @param recipient accountId of recipient.
     */
    void transferMoney(int amount, String recipient);

    /**
     * Transfer money from one account to another.
     *
     * @param amount    amount of money.
     * @param recipient recipient account ID.
     * @param sender    sender account ID.
     */
    void transferMoney(int amount, String recipient, String sender);
}
