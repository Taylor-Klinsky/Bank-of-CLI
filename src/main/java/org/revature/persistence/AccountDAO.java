package org.revature.persistence;

import org.revature.domain.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDAO {

    Account addAccount(int pin);

    /**
     * @param accountNumber
     * @return - the Account with the matching accountNumber (null if not found)
     */
    Account getAccountByAccountNumber(long accountNumber);

    void withdraw(long accountNumber, BigDecimal amount);
    void deposit(long accountNumber, BigDecimal amount);
    void transfer(long fromAccountNumber, long toAccountNumber, BigDecimal amount);
}
