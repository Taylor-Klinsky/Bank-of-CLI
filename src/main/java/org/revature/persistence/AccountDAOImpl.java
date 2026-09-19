package org.revature.persistence;

import org.revature.domain.Account;
import org.revature.exception.AccountNotFoundException;

import javax.xml.transform.Result;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AccountDAOImpl implements AccountDAO{
    // SQL code
    private static final String ADD_ACCOUNT_SQL = """
            INSERT INTO accounts (pin, balance)
            VALUES (?, 0)
            RETURNING account_number, pin, balance
            """;
    private static final String GET_ACCOUNT_BY_ACCOUNT_NUMBER_SQL = """
            SELECT account_number, pin, balance
            FROM accounts
            WHERE account_number = ?
            """;
    private static final String WITHDRAW_FROM_ACCOUNT_SQL = """
            UPDATE accounts SET balance = balance - ? WHERE account_number = ?
            """;
    private static final String DEPOSIT_TO_ACCOUNT_SQL = """
            UPDATE accounts SET balance = balance + ? WHERE account_number = ?
            """;
    private static final String TRANSFER_BETWEEN_ACCOUNTS_SQL = """
            UPDATE accounts SET balance = balance - ? WHERE account_number = ?;
            UPDATE accounts SET balance = balance + ? WHERE account_number = ?;
            """;



    private Connection connection;

    public AccountDAOImpl(Connection connection) { this.connection = connection; }

    @Override
    public Account addAccount(int pin) {
        try (PreparedStatement statement = connection.prepareStatement(ADD_ACCOUNT_SQL)) {
            statement.setInt(1, pin);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new Account(
                            result.getLong("account_number"),
                            result.getInt("pin"),
                            result.getBigDecimal("balance")
                    );
                }
                throw new IllegalStateException("Account was not created");
            }
        } catch (SQLException e) {
            System.out.print("Something went wrong with the database\n");
            return null;
        }
    }

    @Override
    public Account getAccountByAccountNumber(long accountNumber) {
        try (PreparedStatement statement = connection.prepareStatement(GET_ACCOUNT_BY_ACCOUNT_NUMBER_SQL)) {
            statement.setLong(1, accountNumber);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new Account(
                            result.getLong("account_number"),
                            result.getInt("pin"),
                            result.getBigDecimal("balance")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void withdraw(long accountNumber, BigDecimal amount) {
        try (PreparedStatement statement = connection.prepareStatement(WITHDRAW_FROM_ACCOUNT_SQL)) {
            statement.setBigDecimal(1, amount);
            statement.setLong(2, accountNumber);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deposit(long accountNumber, BigDecimal amount) {
        try (PreparedStatement statement = connection.prepareStatement(DEPOSIT_TO_ACCOUNT_SQL)) {
            statement.setBigDecimal(1, amount);
            statement.setLong(2, accountNumber);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void transfer(long fromAccount, long toAccount, BigDecimal amount) {
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement withdraw = connection.prepareStatement(WITHDRAW_FROM_ACCOUNT_SQL);
            PreparedStatement deposit = connection.prepareStatement(DEPOSIT_TO_ACCOUNT_SQL)) {

                withdraw.setBigDecimal(1, amount);
                withdraw.setLong(2, fromAccount);
                withdraw.executeUpdate();

                deposit.setBigDecimal(1, amount);
                deposit.setLong(2, toAccount);
                deposit.executeUpdate();

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
