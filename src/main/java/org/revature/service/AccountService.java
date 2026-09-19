package org.revature.service;

import org.revature.exception.AccountNotFoundException;
import org.revature.exception.InsufficientFundsException;
import org.revature.exception.NotLoggedInException;
import org.revature.exception.InvalidAmountException;

import java.math.BigDecimal;

public interface AccountService {
    /**
     * Creates a new account with a random accountNumber, the given PIN, and a balance of 0.
     * @param pin
     */
    void addAccount(int pin);

    /**
     * Gets the matching account.
     * @param accountNumber
     * @param pin
     * @throws AccountNotFoundException - if account number isn't found, or if pin mismatch
     */
    void logIn(long accountNumber, int pin);

    void logOut();

    /**
     *
     * @param amount
     * @throws InsufficientFundsException
     * @throws InvalidAmountException
     * @throws NotLoggedInException
     *
     */
    void withdraw(BigDecimal amount);

    /**
     *
     * @param amount
     * @throws InvalidAmountException
     * @throws NotLoggedInException
     */
    void deposit(BigDecimal amount);

    /**
     *
     * @param accountNumber
     * @param amount
     * @throws InsufficientFundsException
     * @throws InvalidAmountException
     * @throws NotLoggedInException
     * @throws AccountNotFoundException
     */
    void transfer(long accountNumber, BigDecimal amount);

    /**
     *
     * @return
     * @throws NotLoggedInException
     */
    String[] getTransactions();

    /**
     * @return
     * @throws NotLoggedInException
     */
    BigDecimal getBalance();

    /**
     * @return
     * @throws NotLoggedInException
     */
    long getAccountNumber();

    boolean isLoggedIn();
}
