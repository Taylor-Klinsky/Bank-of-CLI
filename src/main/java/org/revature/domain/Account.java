package org.revature.domain;

public class Account {
    private int id;
    private int pin;
    private double balance;

    public Account(int id, int pin, double balance) {
        this.id = id;
        this.pin = pin;
        this.balance = balance;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    // Other methods
}
