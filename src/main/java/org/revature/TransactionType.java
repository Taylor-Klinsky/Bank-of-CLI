package org.revature;

public enum TransactionType {
    WITHDRAWAL, DEPOSIT, TRANSFER_OUT, TRANSFER_IN;

    public static TransactionType fromString(String value) {
        return valueOf(value);
    }
}
