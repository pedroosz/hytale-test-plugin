package org.example.plugin.coins.events;

import java.util.UUID;

public final class BalanceChangedEvent {

    private final UUID playerId;
    private final long oldBalance;
    private final long newBalance;
    private final long delta;
    private final String reason;
    private final UUID actorId;

    public BalanceChangedEvent(UUID playerId, long oldBalance, long newBalance, long delta, String reason, UUID actorId) {
        this.playerId = playerId;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
        this.delta = delta;
        this.reason = reason;
        this.actorId = actorId;
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

    public String getReason() {
        return reason;
    }

    public UUID getActorId() {
        return actorId;
    }
}
