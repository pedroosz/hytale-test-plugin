package org.example.plugin.coins.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public final class SqliteBalanceRepository implements BalanceRepository {

    private final SqliteDatabase db;

    public SqliteBalanceRepository(SqliteDatabase db) {
        this.db = db;
    }

    @Override
    public long getBalance(UUID playerId) throws SQLException {
        try (Connection c = db.openConnection();
             PreparedStatement ps = c.prepareStatement("SELECT balance FROM balances WHERE player_uuid = ?")) {
            ps.setString(1, playerId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return 0L;
                }
                return rs.getLong(1);
            }
        }
    }

    @Override
    public BalanceUpdate add(UUID playerId, long delta) throws SQLException {
        try (Connection c = db.openConnection()) {
            c.setAutoCommit(false);
            try {
                BalanceUpdate update = addInTx(c, playerId, delta);
                c.commit();
                return update;
            }
            catch (SQLException e) {
                c.rollback();
                throw e;
            }
            finally {
                c.setAutoCommit(true);
            }
        }
    }

    @Override
    public TransferUpdate transfer(UUID fromPlayerId, UUID toPlayerId, long amount) throws SQLException {
        try (Connection c = db.openConnection()) {
            c.setAutoCommit(false);
            try (Statement st = c.createStatement()) {
                st.execute("BEGIN IMMEDIATE");

                long fromOld = getBalanceForUpdate(c, fromPlayerId);
                if (fromOld < amount) {
                    throw new IllegalStateException("insufficient funds");
                }

                BalanceUpdate fromUpdate = setBalanceInTx(c, fromPlayerId, fromOld, fromOld - amount, -amount);

                long toOld = getBalanceForUpdate(c, toPlayerId);
                BalanceUpdate toUpdate = setBalanceInTx(c, toPlayerId, toOld, toOld + amount, amount);

                c.commit();
                return new TransferUpdate(fromUpdate, toUpdate);
            }
            catch (SQLException e) {
                c.rollback();
                throw e;
            }
            finally {
                c.setAutoCommit(true);
            }
        }
    }

    private BalanceUpdate addInTx(Connection c, UUID playerId, long delta) throws SQLException {
        long oldBalance = getBalanceForUpdate(c, playerId);
        long newBalance = oldBalance + delta;
        return setBalanceInTx(c, playerId, oldBalance, newBalance, delta);
    }

    private long getBalanceForUpdate(Connection c, UUID playerId) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT balance FROM balances WHERE player_uuid = ?")) {
            ps.setString(1, playerId.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return 0L;
                }
                return rs.getLong(1);
            }
        }
    }

    private BalanceUpdate setBalanceInTx(Connection c, UUID playerId, long oldBalance, long newBalance, long delta) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO balances(player_uuid, balance) VALUES(?, ?) ON CONFLICT(player_uuid) DO UPDATE SET balance = excluded.balance")) {
            ps.setString(1, playerId.toString());
            ps.setLong(2, newBalance);
            ps.executeUpdate();
        }
        return new BalanceUpdate(playerId, oldBalance, newBalance, delta);
    }
}
