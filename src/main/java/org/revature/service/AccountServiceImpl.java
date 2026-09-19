package org.revature.service;

import org.revature.TransactionType;
import org.revature.domain.Account;
import org.revature.domain.Transaction;
import org.revature.exception.AccountNotFoundException;
import org.revature.exception.InsufficientFundsException;
import org.revature.exception.InvalidAmountException;
import org.revature.exception.NotLoggedInException;
import org.revature.persistence.AccountDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AccountServiceImpl implements AccountService{
    private final AccountDAO accountDAO;
    private final TransactionService transactionService;
    private Account account;
    private Connection connection;

    public AccountServiceImpl(Connection connection, AccountDAO accountDAO, TransactionService transactionService) {
        this.connection = connection;
        this.accountDAO = accountDAO;
        this.transactionService = transactionService;
    }

    @Override
    public void addAccount(String pin) {
        account = accountDAO.addAccount(pin);
    }

    @Override
    public void logIn(long accountNumber, String pin) throws AccountNotFoundException {
        Account account = accountDAO.getAccountByAccountNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        } else if (account.getPin().compareTo(pin) == 0) {
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
    public void withdraw(BigDecimal amount) throws InsufficientFundsException, SQLException {
        requireLoggedIn();
        validateTransaction(amount);
        validateSufficientFunds(amount);

        try {
            connection.setAutoCommit(false);
            accountDAO.withdraw(account.getAccountNumber(), amount);
            account = accountDAO.getAccountByAccountNumber(account.getAccountNumber()); // Update the account to reflect the changes in the database

            transactionService.recordTransaction(account.getAccountNumber(), TransactionType.WITHDRAWAL, amount, null);

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void deposit(BigDecimal amount) throws SQLException{
        requireLoggedIn();
        validateTransaction(amount);

        try {
            connection.setAutoCommit(false);
            accountDAO.deposit(account.getAccountNumber(), amount);
            account = accountDAO.getAccountByAccountNumber(account.getAccountNumber());

            transactionService.recordTransaction(account.getAccountNumber(), TransactionType.DEPOSIT, amount, null);

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void transfer(long accountNumber, BigDecimal amount) throws AccountNotFoundException, InsufficientFundsException, SQLException{
        requireLoggedIn();
        validateTransaction(amount);
        validateSufficientFunds(amount);
        Account toAccount = accountDAO.getAccountByAccountNumber(accountNumber);
        if (toAccount == null) {
            throw new AccountNotFoundException("Destination account not found");
        }

        try {
            connection.setAutoCommit(false);
            accountDAO.transfer(account.getAccountNumber(), toAccount.getAccountNumber(), amount);
            account = accountDAO.getAccountByAccountNumber(account.getAccountNumber());

            transactionService.recordTransaction(account.getAccountNumber(), TransactionType.TRANSFER_OUT, amount, accountNumber);
            transactionService.recordTransaction(accountNumber, TransactionType.TRANSFER_IN, amount, account.getAccountNumber());

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public List<Transaction> getTransactions(Long accountNumber, int quantity){
        requireLoggedIn();
        return transactionService.getTransactions(account.getAccountNumber(), quantity);
    }

    @Override
    public BigDecimal getBalance() throws NotLoggedInException {
        if (!isLoggedIn()) {
            throw new NotLoggedInException("You must be logged in to view your balance");
        }
        return account.getBalance();
    }

    @Override
    public long getAccountNumber() throws NotLoggedInException {
        if (!isLoggedIn()) {
            throw new NotLoggedInException("Please log in");
        }
        return account.getAccountNumber();
    }

    public boolean isLoggedIn() {
        return account != null;
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
