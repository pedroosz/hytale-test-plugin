package org.example.plugin.coins.commands;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import org.example.plugin.coins.BalanceOperations;

import javax.annotation.Nonnull;
import java.util.UUID;

public final class TransferCommand extends CommandBase {

    private final RequiredArg<PlayerRef> targetArg =
            this.withRequiredArg("target", "hycoins.commands.transfer.target", ArgTypes.PLAYER_REF);

    private final RequiredArg<Integer> amountArg =
            this.withRequiredArg("amount", "hycoins.commands.transfer.amount", ArgTypes.INTEGER);

    private final BalanceOperations balanceOperations;

    public TransferCommand(BalanceOperations balanceOperations) {
        super("transfer", "Transfers coins to another player.");
        this.balanceOperations = balanceOperations;
        this.setPermissionGroup(GameMode.Adventure);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        if (!ctx.isPlayer()) {
            ctx.sendMessage(Message.raw("Only players can use this command."));
            return;
        }

        PlayerRef target = targetArg.get(ctx);
        int amountInt = amountArg.get(ctx);

        if (amountInt <= 0) {
            ctx.sendMessage(Message.raw("Amount must be > 0."));
            return;
        }

        UUID fromId = ctx.sender().getUuid();
        UUID toId = target.getUuid();

        try {
            balanceOperations.transfer(fromId, toId, amountInt, "player_transfer", fromId);
            ctx.sendMessage(Message.raw("Transferred " + amountInt + " coins to " + target.getUsername() + "."));
        }
        catch (IllegalStateException e) {
            ctx.sendMessage(Message.raw("Transfer failed: " + e.getMessage()));
        }
        catch (IllegalArgumentException e) {
            ctx.sendMessage(Message.raw("Transfer failed: " + e.getMessage()));
        }
    }
}
