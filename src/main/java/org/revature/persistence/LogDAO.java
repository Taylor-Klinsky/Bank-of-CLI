package org.revature.persistence;

import org.revature.Level;

import java.sql.Timestamp;

public interface LogDAO {
    public void addLog(Long accountNumber, Level level, String message);
}
