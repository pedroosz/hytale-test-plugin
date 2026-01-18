package org.example.plugin.coins.model;

import java.time.Instant;
import java.util.UUID;

public final class Transaction {

    private final UUID id;
    private final UUID fromPlayerId;
    private final UUID toPlayerId;
    private final long amount;
    private final Instant createdAt;
    private final String reason;
    private final UUID actorId;

    public Transaction(UUID id, UUID fromPlayerId, UUID toPlayerId, long amount, Instant createdAt, String reason, UUID actorId) {
        this.id = id;
        this.fromPlayerId = fromPlayerId;
        this.toPlayerId = toPlayerId;
        this.amount = amount;
        this.createdAt = createdAt;
        this.reason = reason;
        this.actorId = actorId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getFromPlayerId() {
        return fromPlayerId;
    }

    public UUID getToPlayerId() {
        return toPlayerId;
    }

    public long getAmount() {
        return amount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public String getReason() {
        return reason;
    }

    public UUID getActorId() {
        return actorId;
    }

    public String toLogLine() {
        String createdAtStr = createdAt != null ? createdAt.toString() : "";
        return id + "|" + fromPlayerId + "|" + toPlayerId + "|" + amount + "|" + createdAtStr + "|" + sanitize(reason) + "|" + actorId;
    }

    private static String sanitize(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\n", " ").replace("\r", " ").replace("|", "/");
    }
}
