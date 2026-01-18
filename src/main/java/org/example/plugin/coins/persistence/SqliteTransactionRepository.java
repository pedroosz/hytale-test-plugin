package org.example.plugin.coins.persistence;

import org.example.plugin.coins.model.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class SqliteTransactionRepository implements TransactionRepository {

    private final SqliteDatabase db;

    public SqliteTransactionRepository(SqliteDatabase db) {
        this.db = db;
    }

    @Override
    public void insert(Transaction tx) throws SQLException {
        try (Connection c = db.openConnection();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO transactions(id, from_player_uuid, to_player_uuid, amount, created_at, reason, actor_uuid) VALUES(?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, tx.getId().toString());
            ps.setString(2, tx.getFromPlayerId().toString());
            ps.setString(3, tx.getToPlayerId().toString());
            ps.setLong(4, tx.getAmount());
            ps.setString(5, tx.getCreatedAt().toString());
            ps.setString(6, tx.getReason());
            ps.setString(7, tx.getActorId() != null ? tx.getActorId().toString() : null);
            ps.executeUpdate();
        }
    }
}
