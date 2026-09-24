package org.revature.persistence;

import org.revature.domain.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
    private static final String SET_PIN_SQL = """
            UPDATE accounts SET pin = ? WHERE account_number = ?
            """;


    private final Connection connection;

    public AccountDAOImpl(Connection connection) { this.connection = connection; }

    @Override
    public Account addAccount(String pin) {
        try (PreparedStatement statement = connection.prepareStatement(ADD_ACCOUNT_SQL)) {
            statement.setString(1, pin);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return new Account(
                            result.getLong("account_number"),
                            result.getString("pin"),
                            result.getBigDecimal("balance")
                    );
                }
                throw new IllegalStateException("Account was not created");
            }
        } catch (SQLException e) {
            System.out.print("Something went wrong with the database while adding the account\n");
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
                            result.getString("pin"),
                            result.getBigDecimal("balance")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            System.out.print("Something went wrong with the database while getting the account\n");
            return null;
        }
    }

    @Override
    public void setPin(long accountNumber, String pin) {
        try (PreparedStatement statement = connection.prepareStatement(SET_PIN_SQL)) {
            statement.setString(1, pin);
            statement.setLong(2, accountNumber);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.print("Something went wrong with the database while updating the pin\n");
        }
    }

    @Override
    public void withdraw(long accountNumber, BigDecimal amount) {
        try (PreparedStatement statement = connection.prepareStatement(WITHDRAW_FROM_ACCOUNT_SQL)) {
            statement.setBigDecimal(1, amount);
            statement.setLong(2, accountNumber);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.print("Something went wrong with the database during a withdrawal\n");
        }
    }

    @Override
    public void deposit(long accountNumber, BigDecimal amount) {
        try (PreparedStatement statement = connection.prepareStatement(DEPOSIT_TO_ACCOUNT_SQL)) {
            statement.setBigDecimal(1, amount);
            statement.setLong(2, accountNumber);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.print("Something went wrong with the database during a deposit\n");
        }
    }

    @Override
    public void transfer(long fromAccount, long toAccount, BigDecimal amount) {
        try {
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
            }

        } catch (SQLException e) {
            System.out.print("Something went wrong with the database during a transfer\n");
        }
    }
}
