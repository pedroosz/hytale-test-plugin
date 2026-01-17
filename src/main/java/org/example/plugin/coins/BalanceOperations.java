package org.example.plugin.coins;

import org.example.plugin.coins.events.BalanceChangedEvent;
import org.example.plugin.coins.events.TransactionCreatedEvent;
import org.example.plugin.coins.model.Transaction;
import org.example.plugin.coins.persistence.BalanceRepository;

import java.time.Instant;
import java.util.UUID;

public final class BalanceOperations {

    private final BalanceRepository balanceRepository;
    private final EventBus eventBus;

    public BalanceOperations(BalanceRepository balanceRepository, EventBus eventBus) {
        this.balanceRepository = balanceRepository;
        this.eventBus = eventBus;
    }

    public long getBalance(UUID playerId) {
        return balanceRepository.getBalance(playerId);
    }

    public synchronized void add(UUID playerId, long delta, String reason, UUID actorId) {
        long oldBalance = balanceRepository.getBalance(playerId);
        long newBalance = oldBalance + delta;

        balanceRepository.setBalance(playerId, newBalance);
        eventBus.publish(new BalanceChangedEvent(playerId, oldBalance, newBalance, delta, reason, actorId));
    }

    public synchronized Transaction transfer(UUID fromPlayerId, UUID toPlayerId, long amount, String reason, UUID actorId) {
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }
        if (fromPlayerId.equals(toPlayerId)) {
            throw new IllegalArgumentException("cannot transfer to self");
        }

        long fromOld = balanceRepository.getBalance(fromPlayerId);
        if (fromOld < amount) {
            throw new IllegalStateException("insufficient funds");
        }

        long toOld = balanceRepository.getBalance(toPlayerId);

        long fromNew = fromOld - amount;
        long toNew = toOld + amount;

        balanceRepository.setBalance(fromPlayerId, fromNew);
        balanceRepository.setBalance(toPlayerId, toNew);

        Transaction tx = new Transaction(UUID.randomUUID(), fromPlayerId, toPlayerId, amount, Instant.now(), reason, actorId);

        eventBus.publish(new BalanceChangedEvent(fromPlayerId, fromOld, fromNew, -amount, reason, actorId));
        eventBus.publish(new BalanceChangedEvent(toPlayerId, toOld, toNew, amount, reason, actorId));
        eventBus.publish(new TransactionCreatedEvent(tx));

        return tx;
    }
}
