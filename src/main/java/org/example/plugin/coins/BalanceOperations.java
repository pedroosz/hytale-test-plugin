package org.example.plugin.coins;

import org.example.plugin.coins.events.BalanceChangedEvent;
import org.example.plugin.coins.events.TransactionCreatedEvent;
import org.example.plugin.coins.model.Transaction;
import org.example.plugin.coins.persistence.BalanceRepository;
import org.example.plugin.coins.persistence.BalanceUpdate;
import org.example.plugin.coins.persistence.TransactionRepository;
import org.example.plugin.coins.persistence.TransferUpdate;

import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

public final class BalanceOperations {

    private final BalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;
    private final EventBus eventBus;

    public BalanceOperations(BalanceRepository balanceRepository, TransactionRepository transactionRepository, EventBus eventBus) {
        this.balanceRepository = balanceRepository;
        this.transactionRepository = transactionRepository;
        this.eventBus = eventBus;
    }

    public long getBalance(UUID playerId) {
        try {
            return balanceRepository.getBalance(playerId);
        }
        catch (SQLException e) {
            throw new IllegalStateException("database error", e);
        }
    }

    public synchronized void add(UUID playerId, long delta, String reason, UUID actorId) {
        try {
            BalanceUpdate update = balanceRepository.add(playerId, delta);
            eventBus.publish(new BalanceChangedEvent(
                    update.getPlayerId(),
                    update.getOldBalance(),
                    update.getNewBalance(),
                    update.getDelta(),
                    reason,
                    actorId));
        }
        catch (SQLException e) {
            throw new IllegalStateException("database error", e);
        }
    }

    public synchronized Transaction transfer(UUID fromPlayerId, UUID toPlayerId, long amount, String reason, UUID actorId) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
        if (fromPlayerId.equals(toPlayerId)) {
            throw new IllegalArgumentException("cannot transfer to self");
        }

        try {
            TransferUpdate transferUpdate = balanceRepository.transfer(fromPlayerId, toPlayerId, amount);

            Transaction tx = new Transaction(UUID.randomUUID(), fromPlayerId, toPlayerId, amount, Instant.now(), reason, actorId);
            transactionRepository.insert(tx);

            eventBus.publish(new BalanceChangedEvent(
                    transferUpdate.getFrom().getPlayerId(),
                    transferUpdate.getFrom().getOldBalance(),
                    transferUpdate.getFrom().getNewBalance(),
                    transferUpdate.getFrom().getDelta(),
                    reason,
                    actorId));

            eventBus.publish(new BalanceChangedEvent(
                    transferUpdate.getTo().getPlayerId(),
                    transferUpdate.getTo().getOldBalance(),
                    transferUpdate.getTo().getNewBalance(),
                    transferUpdate.getTo().getDelta(),
                    reason,
                    actorId));

            eventBus.publish(new TransactionCreatedEvent(tx));
            return tx;
        }
        catch (SQLException e) {
            throw new IllegalStateException("database error", e);
        }
    }
}
