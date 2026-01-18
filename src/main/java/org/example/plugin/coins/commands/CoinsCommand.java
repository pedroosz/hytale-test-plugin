package org.example.plugin.coins.commands;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import org.example.plugin.coins.BalanceOperations;

import javax.annotation.Nonnull;
import java.util.UUID;

public final class CoinsCommand extends CommandBase {

    private final BalanceOperations balanceOperations;

    public CoinsCommand(BalanceOperations balanceOperations) {
        super("coins", "Shows your current coin balance.");
        this.balanceOperations = balanceOperations;
        this.setPermissionGroup(GameMode.Adventure);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("Only players can use this command."));
            return;
        }

        UUID playerId = ctx.sender().getUuid();
        long balance = balanceOperations.getBalance(playerId);
        ctx.sendMessage(Message.raw("Coins: " + balance));
    }
}
