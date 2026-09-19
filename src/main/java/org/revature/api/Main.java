package org.revature.api;

import org.revature.persistence.*;
import org.revature.service.*;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try {
            Connection connection = ConnectionFactory.getConnectionFactory().getConnection();
            AccountDAO accountDAO = new AccountDAOImpl(connection);
            // TransactionDAO transactionDAO = new TransactionDAOImpl(connection);
            // TransactionService transactionService = new TransactionServiceImpl(transactionDAO);
            AccountService accountService = new AccountServiceImpl(accountDAO, null);
            new BankRepl(accountService).run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
