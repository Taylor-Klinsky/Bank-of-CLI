package org.revature.persistence;

import org.revature.domain.Account;

import java.util.List;

public class AccountDAOImpl implements AccountDAO{

    private final String fileName;

    public AccountDAOImpl(String fileName) { this.fileName = fileName; }

    @Override
    public void addAccount(Account account) {

    }

    @Override
    public Account getAccountById(int id) {
        return null;
    }

    @Override
    public void withdraw(Account account, double amount) {

    }

    @Override
    public void deposit(Account account, double amount) {

    }

    @Override
    public void transfer(Account fromAccount, Account toAccount, double amount) {

    }

    @Override
    public List<Account> getAllAccounts() {
        return List.of();
    }
}
