package org.example.plugin.coins.persistence;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class SqliteDatabase {

    private final String jdbcUrl;

    public SqliteDatabase(Path dbFile) {
        this.jdbcUrl = "jdbc:sqlite:" + dbFile.toAbsolutePath();
    }

    private static void ensureDriverLoaded() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        }
        catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver class not found", e);
        }
    }

    public Connection openConnection() throws SQLException {
        ensureDriverLoaded();
        Connection c = DriverManager.getConnection(jdbcUrl);
        try (Statement st = c.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
            st.execute("PRAGMA journal_mode = WAL");
            st.execute("PRAGMA synchronous = NORMAL");
        }
        return c;
    }

    public void initSchema() throws SQLException {
        try (Connection c = openConnection(); Statement st = c.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS balances (player_uuid TEXT PRIMARY KEY, balance INTEGER NOT NULL)");
            st.execute("CREATE TABLE IF NOT EXISTS transactions (id TEXT PRIMARY KEY, from_player_uuid TEXT NOT NULL, to_player_uuid TEXT NOT NULL, amount INTEGER NOT NULL, created_at TEXT NOT NULL, reason TEXT, actor_uuid TEXT)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_transactions_from ON transactions(from_player_uuid)");
            st.execute("CREATE INDEX IF NOT EXISTS idx_transactions_to ON transactions(to_player_uuid)");
        }
    }
}
