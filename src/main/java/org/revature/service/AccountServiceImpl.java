package org.revature.service;

import org.revature.domain.Account;
import org.revature.persistence.AccountDAO;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

public class AccountServiceImpl implements AccountService{
    private final AccountDAO accountDAO;
    private Account account;

    public AccountServiceImpl(AccountDAO accountDAO) { this.accountDAO = accountDAO; }

    // TODO: implement addAccount
    @Override
    public void addAccount(int pin) {
        // generate a new account with the given pin
    }

    @Override
    public Account findAccount(long accountNumber, int pin) throws NoSuchElementException {
        Account account = accountDAO.getAccountById(accountNumber);
        if (account.getPin() == pin) {
            return account;
        } else {
            throw new NoSuchElementException("This ID and PIN combination was not found");
        }
    }

    @Override
    public void withdraw(BigDecimal amount) throws IllegalArgumentException {
        if (amount.compareTo(new BigDecimal(0)) < 0) {
            throw new IllegalArgumentException("Cannot withdraw a negative amount");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        } else {
            accountDAO.withdraw(account, amount);
        }
    }

    @Override
    public void deposit(BigDecimal amount) throws IllegalArgumentException {
        if (amount.compareTo(new BigDecimal(0)) < 0) {
            throw new IllegalArgumentException("Cannot deposit a negative amount");
        } else {
            accountDAO.deposit(account, amount);
        }
    }

    @Override
    public void transfer(Account fromAccount, Account toAccount, BigDecimal amount) throws IllegalArgumentException{
        if (amount.compareTo(new BigDecimal(0)) < 0) {
            throw new IllegalArgumentException("Cannot transfer a negative amount");
        }
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        } else {
            accountDAO.transfer(fromAccount, toAccount, amount);
        }
    }
}
