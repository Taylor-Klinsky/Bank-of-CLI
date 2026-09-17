package org.revature.service;

import org.revature.domain.Account;

import java.math.BigDecimal;

public interface AccountService {
    void addAccount(int pin);
    public Account findAccount(long accountNumber, int pin);

    void withdraw(BigDecimal amount) throws IllegalArgumentException;

    void deposit(BigDecimal amount) throws IllegalArgumentException;

    void transfer(Account fromAccount, Account toAccount, BigDecimal amount);
}
