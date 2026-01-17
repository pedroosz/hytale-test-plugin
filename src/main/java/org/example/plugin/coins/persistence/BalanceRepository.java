package org.example.plugin.coins.persistence;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class BalanceRepository {

    private final Path file;
    private final Map<UUID, Long> balances = new ConcurrentHashMap<>();

    public BalanceRepository(Path dataDir) {
        this.file = dataDir.resolve("balances.properties");
    }

    public void load() throws IOException {
        balances.clear();
        if (!Files.exists(file)) {
            return;
        }

        Properties props = new Properties();
        try (InputStream in = new BufferedInputStream(Files.newInputStream(file))) {
            props.load(in);
        }

        for (String key : props.stringPropertyNames()) {
            try {
                UUID id = UUID.fromString(key);
                long value = Long.parseLong(props.getProperty(key));
                balances.put(id, value);
            }
            catch (Exception ignored) {
            }
        }
    }

    public synchronized void save() throws IOException {
        Properties props = new Properties();
        for (Map.Entry<UUID, Long> entry : balances.entrySet()) {
            props.setProperty(entry.getKey().toString(), Long.toString(entry.getValue()));
        }

        Files.createDirectories(file.getParent());
        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(file))) {
            props.store(out, null);
        }
    }

    public long getBalance(UUID playerId) {
        return balances.getOrDefault(playerId, 0L);
    }

    public void setBalance(UUID playerId, long newBalance) {
        balances.put(playerId, newBalance);
    }
}
