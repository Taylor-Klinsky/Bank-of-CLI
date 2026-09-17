package org.revature.persistence;

import org.revature.domain.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDAO {
    void addAccount(Account account);
    Account getAccountById(long accountNumber);
    void withdraw(Account account, BigDecimal amount);
    void deposit(Account account, BigDecimal amount);
    void transfer(Account fromAccount, Account toAccount, BigDecimal amount);
    List<Account> getAllAccounts();
}
