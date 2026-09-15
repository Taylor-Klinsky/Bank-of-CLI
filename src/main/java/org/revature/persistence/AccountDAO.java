package org.revature.persistence;

import org.revature.domain.Account;

import java.util.List;

public interface AccountDAO {
    void addAccount(Account account);
    Account getAccountById(int id);
    void withdraw(Account account, double amount);
    void deposit(Account fromAccount, Account toAccount, double amount);
    List<Account> getAllAccounts();
}
