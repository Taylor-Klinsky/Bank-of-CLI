package org.revature.api;

import org.revature.persistence.*;
import org.revature.service.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Main.class);

        try {
            Connection connection = ConnectionFactory.getConnectionFactory().getConnection();
            AccountDAO accountDAO = new AccountDAOImpl(connection);
            TransactionDAO transactionDAO = new TransactionDAOImpl(connection);
            TransactionService transactionService = new TransactionServiceImpl(transactionDAO);
            AccountService accountService = new AccountServiceImpl(connection, accountDAO, transactionService);
            new BankRepl(accountService).run();
        } catch (Exception e) {
            logger.error("Could not connect to Postgres database");
            System.out.println(e.getMessage());
        }
    }
}
