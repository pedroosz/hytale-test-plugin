package org.example.plugin.coins.persistence;

import java.sql.SQLException;
import java.util.UUID;

public interface BalanceRepository {

    long getBalance(UUID playerId) throws SQLException;

    BalanceUpdate add(UUID playerId, long delta) throws SQLException;

    TransferUpdate transfer(UUID fromPlayerId, UUID toPlayerId, long amount) throws SQLException;
}
