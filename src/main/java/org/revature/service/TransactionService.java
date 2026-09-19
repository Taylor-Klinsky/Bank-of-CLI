package org.revature.service;

import org.revature.TransactionType;
import org.revature.domain.Transaction;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    List<Transaction> getTransactions(Long accountNumber, int quantity);
    void recordTransaction(long accountNumber, TransactionType type, BigDecimal amount, Long relatedAccount);
}
