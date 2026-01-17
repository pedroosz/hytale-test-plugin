package org.example.plugin.coins;

import com.hypixel.hytale.logger.HytaleLogger;
import org.example.plugin.coins.events.BalanceChangedEvent;
import org.example.plugin.coins.events.TransactionCreatedEvent;
import org.example.plugin.coins.persistence.BalanceRepository;
import org.example.plugin.coins.persistence.TransactionRepository;

import java.io.IOException;

public final class CoinPluginModule {

    private final HytaleLogger logger;
    private final EventBus eventBus;
    private final BalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;
    private final BalanceOperations balanceOperations;

    public CoinPluginModule(HytaleLogger logger, EventBus eventBus, BalanceRepository balanceRepository, TransactionRepository transactionRepository) {
        this.logger = logger;
        this.eventBus = eventBus;
        this.balanceRepository = balanceRepository;
        this.transactionRepository = transactionRepository;
        this.balanceOperations = new BalanceOperations(balanceRepository, eventBus);

        wirePersistenceHandlers();
    }

    public BalanceOperations getBalanceOperations() {
        return balanceOperations;
    }

    private void wirePersistenceHandlers() {
        eventBus.subscribe(BalanceChangedEvent.class, evt -> {
            try {
                balanceRepository.save();
            }
            catch (IOException e) {
                logger.atSevere().withCause(e).log("Failed to persist balances");
            }
        });

        eventBus.subscribe(TransactionCreatedEvent.class, evt -> {
            try {
                transactionRepository.append(evt.getTransaction());
            }
            catch (IOException e) {
                logger.atSevere().withCause(e).log("Failed to persist transaction");
            }
        });
    }
}
