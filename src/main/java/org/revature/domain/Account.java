package org.revature.domain;

import java.math.BigDecimal;

public class Account {
    private Long accountNumber;
    private String pin;
    private BigDecimal balance;

    public Account() {}

    public Account(String pin) {
        this.pin = pin;
        balance = new BigDecimal(0);
    }

    public Account(long accountNumber, String pin, BigDecimal balance) {
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

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
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
