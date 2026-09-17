package org.revature.domain;

import java.math.BigDecimal;

public class Account {
    private Long accountNumber;
    private int pin;
    private BigDecimal balance;

    public Account() {}

    public Account(int pin) {
        this.pin = pin;
        balance = new BigDecimal(0);
    }

    public Account(long accountNumber, int pin, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = balance;
    }

    // Getters and setters
    public long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    // Other methods
}
