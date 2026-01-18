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

public final class AddCommand extends CommandBase {
    private final RequiredArg<PlayerRef> targetArg =
            this.withRequiredArg("target", "hycoins.commands.add.target", ArgTypes.PLAYER_REF);
    private final RequiredArg<Integer> amountArg =
            this.withRequiredArg("amount", "hycoins.commands.add.amount", ArgTypes.INTEGER);


    private final BalanceOperations balanceOperations;

    public AddCommand(BalanceOperations balanceOperations) {
        super("add", "Adds (or removes) coins from a player.");
        this.balanceOperations = balanceOperations;
        this.setPermissionGroup(GameMode.Creative);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        PlayerRef target = targetArg.get(ctx);
        int amountInt = amountArg.get(ctx);

        balanceOperations.add(target.getUuid(), amountInt, "admin_add", ctx.sender().getUuid());
        ctx.sendMessage(Message.raw("Updated " + target.getUsername() + " balance by " + amountInt + " coins."));
    }
}
