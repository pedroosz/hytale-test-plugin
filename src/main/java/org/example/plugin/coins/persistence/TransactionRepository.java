package org.example.plugin.coins.persistence;

import org.example.plugin.coins.model.Transaction;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class TransactionRepository {

    private final Path file;

    public TransactionRepository(Path dataDir) {
        this.file = dataDir.resolve("transactions.log");
    }

    public synchronized void append(Transaction tx) throws IOException {
        Files.createDirectories(file.getParent());
        try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            w.write(tx.toLogLine());
            w.newLine();
        }
    }
}
