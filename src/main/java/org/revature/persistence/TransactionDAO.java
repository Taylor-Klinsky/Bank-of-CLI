package org.revature.persistence;

import org.revature.TransactionType;
import org.revature.domain.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionDAO {
    void recordTransaction(long accountNumber, TransactionType type, BigDecimal amount, Long relatedAccount);
    List<Transaction> getRecentTransactions(long accountNumber, int quantity);
    List<Transaction> getAllTransactions(long accountNumber);
}