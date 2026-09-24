package org.revature.persistence;

import org.revature.domain.Account;

import java.math.BigDecimal;

public interface AccountDAO {

    Account addAccount(String pin);

    /**
     * @param accountNumber
     * @return - the Account with the matching accountNumber (null if not found)
     */
    Account getAccountByAccountNumber(long accountNumber);

    void setPin(long accountNumber, String pin);

    void withdraw(long accountNumber, BigDecimal amount);
    void deposit(long accountNumber, BigDecimal amount);
    void transfer(long fromAccountNumber, long toAccountNumber, BigDecimal amount);
}
