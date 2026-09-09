package org.revature.business;

import java.math.*;

public class Business {
    int accountID;

    public Business (int accountID) {
        this.accountID = accountID;
    }

    static boolean validateLogin(int accountID, int PIN) {

    }

    // TODO: implement Deposit
    boolean Deposit(BigDecimal amount) {
        return true;
    }

    // TODO: implement Withdraw
    boolean Withdraw(BigDecimal amount) {
        return true;
    }

    // TODO: implement Transfer
    boolean Transfer(BigDecimal amount, int transferAccountID) {
        return true;
    }
}
