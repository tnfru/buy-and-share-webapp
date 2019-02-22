package de.hhu.propra.sharingplatform.service;

import org.springframework.stereotype.Component;

@Component
public interface IBankAccountService {
    int getAccountBalance(String accountname);
    void transferMoney(int amount, String recipient);
    void transferMoney(int amount, String recipient, String sender);
}
