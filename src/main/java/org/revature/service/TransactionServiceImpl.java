package org.revature.service;

import org.revature.TransactionType;
import org.revature.domain.Transaction;
import org.revature.persistence.TransactionDAO;

import java.math.BigDecimal;
import java.util.List;

public class TransactionServiceImpl implements TransactionService{
    private final TransactionDAO transactionDAO;

    public TransactionServiceImpl(TransactionDAO transactionDAO) {
        this.transactionDAO = transactionDAO;
    }

    @Override
    public List<Transaction> getTransactions(Long accountNumber, int quantity) {
        if (quantity == 0) {
            return transactionDAO.getAllTransactions(accountNumber);
        } else {
            return transactionDAO.getRecentTransactions(accountNumber, quantity);
        }
    }

    @Override
    public void recordTransaction(long accountNumber, TransactionType type, BigDecimal amount, Long relatedAccount) {
        transactionDAO.recordTransaction(accountNumber, type, amount, relatedAccount);
    }
}
