package org.revature.service;

import org.revature.domain.Account;
import org.revature.exception.AccountNotFoundException;
import org.revature.exception.InsufficientFundsException;
import org.revature.exception.InvalidAmountException;
import org.revature.exception.NotLoggedInException;
import org.revature.persistence.AccountDAO;

import java.math.BigDecimal;

public class AccountServiceImpl implements AccountService{
    private final AccountDAO accountDAO;
    private final TransactionService transactionService;
    private Account account;

    public AccountServiceImpl(AccountDAO accountDAO, TransactionService transactionService) {
        this.accountDAO = accountDAO;
        this.transactionService = transactionService;
    }

    @Override
    public void addAccount(int pin) {
        account = accountDAO.addAccount(pin);
    }

    @Override
    public void logIn(long accountNumber, int pin) throws AccountNotFoundException {
        Account account = accountDAO.getAccountByAccountNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        } else if (account.getPin() == pin) {
            this.account = account;
        } else {
            throw new AccountNotFoundException("Incorrect PIN");
        }
    }

    @Override
    public void logOut() {
        account = null;
    }

    @Override
    public void withdraw(BigDecimal amount) throws InsufficientFundsException {
        validateTransaction(amount);
        validateSufficientFunds(amount);
        accountDAO.withdraw(account.getAccountNumber(), amount);
        account = accountDAO.getAccountByAccountNumber(account.getAccountNumber()); // Update the account to reflect the changes in the database
    }

    @Override
    public void deposit(BigDecimal amount) {
        validateTransaction(amount);
        accountDAO.deposit(account.getAccountNumber(), amount);
        account = accountDAO.getAccountByAccountNumber(account.getAccountNumber());
    }

    @Override
    public void transfer(long accountNumber, BigDecimal amount) throws AccountNotFoundException, InsufficientFundsException{
        validateTransaction(amount);
        validateSufficientFunds(amount);
        Account toAccount = accountDAO.getAccountByAccountNumber(accountNumber);
        accountDAO.transfer(account.getAccountNumber(), toAccount.getAccountNumber(), amount);
        account = accountDAO.getAccountByAccountNumber(account.getAccountNumber());
    }

    @Override
    public String[] getTransactions() {
        return null;
    }

    @Override
    public BigDecimal getBalance() throws NotLoggedInException {
        if (account == null) {
            throw new NotLoggedInException("Please log in");
        }
        return account.getBalance();
    }

    @Override
    public long getAccountNumber() throws NotLoggedInException {
        if (account == null) {
            throw new NotLoggedInException("Please log in");
        }
        return account.getAccountNumber();
    }

    public boolean isLoggedIn() {
        if (account == null) {
            return false;
        } else {
            return true;
        }
    }

    // Helper functions
    private void validateAmount(BigDecimal num) throws InvalidAmountException{
        if (num.scale() > 2) {
            throw new InvalidAmountException("Cannot accept fractional cents");
        } else if (num.compareTo(new BigDecimal(0)) < 0) {
            throw new InvalidAmountException("Cannot accept negative input");
        }
    }

    private void requireLoggedIn() throws NotLoggedInException {
        if (!isLoggedIn()) {
            throw new NotLoggedInException("Please log in");
        }
    }

    private void validateTransaction(BigDecimal amount) {
        validateAmount(amount);
        requireLoggedIn();
    }

    private void validateSufficientFunds(BigDecimal amount) throws InsufficientFundsException {
        if (amount.compareTo(account.getBalance()) > 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
    }
}
