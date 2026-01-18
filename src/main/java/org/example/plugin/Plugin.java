package org.example.plugin;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import org.example.plugin.coins.CoinPluginModule;
import org.example.plugin.coins.SimpleEventBus;
import org.example.plugin.coins.commands.AddCommand;
import org.example.plugin.coins.commands.CoinsCommand;
import org.example.plugin.coins.commands.TransferCommand;
import org.example.plugin.coins.persistence.BalanceRepository;
import org.example.plugin.coins.persistence.SqliteBalanceRepository;
import org.example.plugin.coins.persistence.SqliteDatabase;
import org.example.plugin.coins.persistence.SqliteTransactionRepository;
import org.example.plugin.coins.persistence.TransactionRepository;

import javax.annotation.Nonnull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

/**
 * This class serves as the entrypoint for your plugin. Use the setup method to register into game registries or add
 * event listeners.
 */
public class Plugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public Plugin(@Nonnull JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Hello from " + this.getName() + " version " + this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up plugin " + this.getName());

        Path dataDir = this.getDataDirectory();
        try {
            Files.createDirectories(dataDir);
        }
        catch (IOException e) {
            LOGGER.atSevere().withCause(e).log("Failed to create plugin data directory at " + dataDir);
        }

        SqliteDatabase db = new SqliteDatabase(dataDir.resolve("coins.db"));
        try {
            db.initSchema();
        }
        catch (SQLException e) {
            LOGGER.atSevere().withCause(e).log("Failed to initialize SQLite schema");
            return;
        }

        BalanceRepository balanceRepository = new SqliteBalanceRepository(db);
        TransactionRepository transactionRepository = new SqliteTransactionRepository(db);
        SimpleEventBus eventBus = new SimpleEventBus();
        CoinPluginModule module = new CoinPluginModule(LOGGER, eventBus, balanceRepository, transactionRepository);

        this.getCommandRegistry().registerCommand(new CoinsCommand(module.getBalanceOperations()));
        this.getCommandRegistry().registerCommand(new TransferCommand(module.getBalanceOperations()));
        this.getCommandRegistry().registerCommand(new AddCommand(module.getBalanceOperations()));
    }
}