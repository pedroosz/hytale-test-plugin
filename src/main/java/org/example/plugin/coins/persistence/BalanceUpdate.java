package org.example.plugin.coins.persistence;

import java.util.UUID;

public final class BalanceUpdate {

    private final UUID playerId;
    private final long oldBalance;
    private final long newBalance;
    private final long delta;

    public BalanceUpdate(UUID playerId, long oldBalance, long newBalance, long delta) {
        this.playerId = playerId;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
        this.delta = delta;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public long getOldBalance() {
        return oldBalance;
    }

    public long getNewBalance() {
        return newBalance;
    }

    public long getDelta() {
        return delta;
    }
}
