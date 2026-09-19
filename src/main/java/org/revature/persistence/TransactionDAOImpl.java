package org.revature.persistence;

import org.revature.TransactionType;
import org.revature.domain.Transaction;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImpl implements TransactionDAO{
    private static final String RECORD_TRANSACTION_SQL = """
            INSERT INTO transactions (account_number, type, amount, related_account)
            VALUES (?, ?, ?, ?)
            """;
    private static final String LIST_RECENT_TRANSACTIONS_SQL = """
            SELECT id, account_number, type, amount, timestamp, related_account
            FROM transactions
            WHERE account_number = ?
            ORDER BY timestamp DESC
            LIMIT ?
            """;
    private static final String LIST_ALL_TRANSACTIONS_SQL = """
            SELECT id, account_number, type, amount, timestamp, related_account
            FROM transactions
            WHERE account_number = ?
            ORDER BY timestamp DESC
            """;

    private final Connection connection;

    public TransactionDAOImpl(Connection connection) { this.connection = connection; }

    @Override
    public void recordTransaction(long accountNumber, TransactionType type, BigDecimal amount, Long relatedAccount) {
        try (PreparedStatement statement = connection.prepareStatement(RECORD_TRANSACTION_SQL)) {
            statement.setLong(1, accountNumber);
            statement.setString(2, String.valueOf(type));
            statement.setBigDecimal(3, amount);
            statement.setObject(4, relatedAccount); // This value can be null
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.print("Something went wrong recording that transaction");
        }
    }

    @Override
    public List<Transaction> getRecentTransactions(long accountNumber, int quantity) {
        try (PreparedStatement statement = connection.prepareStatement(LIST_RECENT_TRANSACTIONS_SQL)) {
            statement.setLong(1, accountNumber);
            statement.setInt(2, quantity);

            try (ResultSet result = statement.executeQuery()) {
                List<Transaction> transactions = new ArrayList<>();
                while (result.next()) {
                    transactions.add(new Transaction(
                            result.getLong("account_number"),
                            TransactionType.fromString(result.getString("type")),
                            result.getBigDecimal("amount"),
                            result.getObject("related_account", Long.class),
                            result.getTimestamp("timestamp"),
                            result.getLong("id")
                    ));
                }
                return transactions;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Transaction> getAllTransactions(long accountNumber) {
        try (PreparedStatement statement = connection.prepareStatement(LIST_ALL_TRANSACTIONS_SQL)) {
            statement.setLong(1, accountNumber);

            try (ResultSet result = statement.executeQuery()) {
                List<Transaction> transactions = new ArrayList<>();
                while (result.next()) {
                    transactions.add(new Transaction(
                            result.getLong("account_number"),
                            TransactionType.fromString(result.getString("type")),
                            result.getBigDecimal("amount"),
                            result.getObject("related_account", Long.class),
                            result.getTimestamp("timestamp"),
                            result.getLong("id")
                    ));
                }
                return transactions;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
