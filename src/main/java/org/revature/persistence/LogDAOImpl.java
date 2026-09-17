package org.revature.persistence;

import org.revature.Level;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class LogDAOImpl implements LogDAO {
    private Connection connection;

    public LogDAOImpl (Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addLog(Long accountNumber, Level level, String message) {
        String sql = """
                INSERT INTO interaction_logs (timestamp, account_number, level, message)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            if (accountNumber == null) {
                statement.setNull(1, java.sql.Types.BIGINT);
            } else {
                statement.setLong(1, accountNumber);
            }
            statement.setString(2, String.valueOf(level));
            statement.setString(3, message);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
