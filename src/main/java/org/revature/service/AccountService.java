package org.revature.service;

import org.revature.domain.Account;

public interface AccountService {
    void addAccount(int pin);
    public Account findAccount(int id, int pin);
    void withdraw(Account account, double amount);
    void deposit(Account account, double amount);
    void transfer(Account fromAccount, Account toAccount, double amount);
}
