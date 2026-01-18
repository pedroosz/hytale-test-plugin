package org.example.plugin.coins.events;

import org.example.plugin.coins.model.Transaction;

public final class TransactionCreatedEvent {

    private final Transaction transaction;

    public TransactionCreatedEvent(Transaction transaction) {
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }
}
