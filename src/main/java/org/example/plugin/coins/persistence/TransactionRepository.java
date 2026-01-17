package org.example.plugin.coins.persistence;

import org.example.plugin.coins.model.Transaction;

import java.sql.SQLException;

public interface TransactionRepository {

    void insert(Transaction tx) throws SQLException;
}
