package org.example.plugin.coins;

import com.hypixel.hytale.logger.HytaleLogger;
import org.example.plugin.coins.persistence.BalanceRepository;
import org.example.plugin.coins.persistence.TransactionRepository;

public final class CoinPluginModule {

    private final HytaleLogger logger;
    private final EventBus eventBus;
    private final BalanceOperations balanceOperations;

    public CoinPluginModule(HytaleLogger logger, EventBus eventBus, BalanceRepository balanceRepository, TransactionRepository transactionRepository) {
        this.logger = logger;
        this.eventBus = eventBus;
        this.balanceOperations = new BalanceOperations(balanceRepository, transactionRepository, eventBus);
    }

    public BalanceOperations getBalanceOperations() {
        return balanceOperations;
    }
}
