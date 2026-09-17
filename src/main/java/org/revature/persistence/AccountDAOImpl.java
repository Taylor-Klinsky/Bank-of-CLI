package org.revature.persistence;

import org.revature.domain.Account;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AccountDAOImpl implements AccountDAO{

    private Connection connection;

    public AccountDAOImpl(Connection connection) { this.connection = connection; }

    @Override
    public void addAccount(Account account) {
        String sql = """
                INSERT INTO accounts (account_number, pin, balance)
                VALUES (?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, account.getAccountNumber());
            statement.setInt(2, account.getPin());
            statement.setBigDecimal(3, account.getBalance());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Account getAccountById(long id) {
        return null;
    }

    @Override
    public void withdraw(Account account, BigDecimal amount) {

    }

    @Override
    public void deposit(Account account, BigDecimal amount) {

    }

    @Override
    public void transfer(Account fromAccount, Account toAccount, BigDecimal amount) {

    }

    @Override
    public List<Account> getAllAccounts() {
        return List.of();
    }
}
