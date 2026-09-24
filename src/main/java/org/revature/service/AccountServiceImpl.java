package org.revature.service;

import org.revature.TransactionType;
import org.revature.domain.Account;
import org.revature.domain.Transaction;
import org.revature.exception.*;
import org.revature.persistence.AccountDAO;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AccountServiceImpl implements AccountService{
    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);
    private final AccountDAO accountDAO;
    private final TransactionService transactionService;
    private final Connection connection;
    private Account account;

    public AccountServiceImpl(Connection connection, AccountDAO accountDAO, TransactionService transactionService) {
        this.connection = connection;
        this.accountDAO = accountDAO;
        this.transactionService = transactionService;
    }

    @Override
    public void addAccount(String pin) {
        account = accountDAO.addAccount(pin);
        logger.info("Account {} created", account.getAccountNumber());
    }

    @Override
    public void logIn(long accountNumber, String pin) throws AccountNotFoundException, InvalidCredentialException {
        logOut();
        Account account = accountDAO.getAccountByAccountNumber(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Account not found");
        }
        checkPin(accountNumber, pin);
        logger.info("Logged into account {}", accountNumber);
        this.account = account;
    }

    @Override
    public void logOut() {
        if (account != null) {
            logger.info("Logged out of account {}", account.getAccountNumber());
        }
        account = null;
    }

    @Override
    public void withdraw(BigDecimal amount) throws InsufficientFundsException, SQLException {
        validateTransaction(amount);
        try {
            validateSufficientFunds(amount);
        } catch (InsufficientFundsException e) {
            logger.warn("Withdrawal of ${} from account {} rejected: insufficient funds", amount, account.getAccountNumber());
            throw e;
        }

        try {
            connection.setAutoCommit(false);
            accountDAO.withdraw(account.getAccountNumber(), amount);
            account = accountDAO.getAccountByAccountNumber(account.getAccountNumber()); // Update the account to reflect the changes in the database

            transactionService.recordTransaction(account.getAccountNumber(), TransactionType.WITHDRAWAL, amount, null);

            connection.commit();
            logger.info("Withdrawal of ${} from account {} completed", amount, account.getAccountNumber());
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Database error during withdrawal of ${} from account {}", amount, account.getAccountNumber());
            throw e;
        }
        finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void deposit(BigDecimal amount) throws SQLException{
        validateTransaction(amount);

        try {
            connection.setAutoCommit(false);
            accountDAO.deposit(account.getAccountNumber(), amount);
            account = accountDAO.getAccountByAccountNumber(account.getAccountNumber());

            transactionService.recordTransaction(account.getAccountNumber(), TransactionType.DEPOSIT, amount, null);

            connection.commit();
            logger.info("Deposit of ${} to account {} completed", amount, account.getAccountNumber());
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Database error during deposit of ${} to account {}", amount, account.getAccountNumber());
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void transfer(long accountNumber, BigDecimal amount) throws AccountNotFoundException, InsufficientFundsException, SQLException{
        validateTransaction(amount);

        try {
            validateSufficientFunds(amount);
        } catch (InsufficientFundsException e) {
            logger.warn("Transfer of ${} from account {} to account {} rejected: insufficient funds", amount, account.getAccountNumber(), accountNumber);
            throw e;
        }
        Account toAccount = accountDAO.getAccountByAccountNumber(accountNumber);
        if (toAccount == null) {
            logger.warn("Transfer of ${} from account {} to account {} rejected: destination doesn't exist", amount, account.getAccountNumber(), accountNumber);
            throw new AccountNotFoundException("Destination account not found");
        }

        try {
            connection.setAutoCommit(false);
            accountDAO.transfer(account.getAccountNumber(), toAccount.getAccountNumber(), amount);
            account = accountDAO.getAccountByAccountNumber(account.getAccountNumber());

            transactionService.recordTransaction(account.getAccountNumber(), TransactionType.TRANSFER_OUT, amount, accountNumber);
            transactionService.recordTransaction(accountNumber, TransactionType.TRANSFER_IN, amount, account.getAccountNumber());

            connection.commit();
            logger.info("Transfer of ${} from account {} to account {} completed", amount, account.getAccountNumber(), accountNumber);
        } catch (SQLException e) {
            connection.rollback();
            logger.error("Database error during transfer of ${} from account {} to account {}", amount, account.getAccountNumber(), accountNumber);
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    @Override
    public void setPin(String pin) {
        requireLoggedIn();
        validatePinFormat(pin);
        accountDAO.setPin(account.getAccountNumber(), pin);
        account.setPin(pin);
        logger.info("Update of PIN for account {} completed", account.getAccountNumber());
    }

    @Override
    public void checkPin(long accountNumber, String pin) throws InvalidCredentialException, AccountNotFoundException {
        Account currAccount = accountDAO.getAccountByAccountNumber(accountNumber);
        if (currAccount == null) {
            throw new AccountNotFoundException("Account not found");
        }
        if (currAccount.getPin().compareTo(pin) != 0) {
            logger.warn("Incorrect PIN entered for account {}", accountNumber);
            throw new InvalidCredentialException("Incorrect PIN");
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

    @Override
    public void validatePinFormat(String input) throws IllegalArgumentException {
        if (!input.matches("\\d{4}")) {
            throw new IllegalArgumentException("PIN must be 4 digits");
        }
    }
}
