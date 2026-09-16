package org.revature.service;

import org.revature.domain.Account;
import org.revature.persistence.AccountDAO;

import java.util.NoSuchElementException;

public class AccountServiceImpl implements AccountService{
    private final AccountDAO accountDAO;

    public AccountServiceImpl(AccountDAO accountDAO) { this.accountDAO = accountDAO; }

    // TODO: implement addAccount
    @Override
    public void addAccount(int pin) {
        // generate a new account with the given pin
    }

    @Override
    public Account findAccount(int id, int pin) throws NoSuchElementException {
        Account account = accountDAO.getAccountById(id);
        if (account.getPin() == pin) {
            return account;
        } else {
            throw new NoSuchElementException("This ID and PIN combination was not found");
        }
    }

    @Override
    public void withdraw(Account account, double amount) throws IllegalArgumentException {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot withdraw a negative amount");
        }
        if (account.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        } else {
            accountDAO.withdraw(account, amount);
        }
    }

    @Override
    public void deposit(Account account, double amount) throws IllegalArgumentException {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot deposit a negative amount");
        } else {
            accountDAO.deposit(account, amount);
        }
    }

    @Override
    public void transfer(Account fromAccount, Account toAccount, double amount) throws IllegalArgumentException{
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot transfer a negative amount");
        }
        if (fromAccount.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds");
        } else {
            accountDAO.transfer(fromAccount, toAccount, amount);
        }
    }
}
