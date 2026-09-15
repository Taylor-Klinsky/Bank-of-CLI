package org.revature.service;

import org.revature.domain.Account;
import org.revature.persistence.AccountDAO;

import java.util.List;
import java.util.NoSuchElementException;

public class AccountServiceImpl implements AccountService{
    private final AccountDAO accountDAO;

    public AccountServiceImpl(AccountDAO accountDAO) { this.accountDAO = accountDAO; }

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
    public void withdraw(Account account, double amount) {

    }

    @Override
    public void deposit(Account fromAccount, Account toAccount, double amount) {

    }
}
