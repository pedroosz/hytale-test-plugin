package org.example.plugin;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;

import javax.annotation.Nonnull;

/**
 * This is an example command that will simply print the name of the plugin in chat when used.
 */
public class ExampleCommand extends CommandBase {

    private final RequiredArg<String> nameArg =
            this.withRequiredArg("name", "myplugin.commands.echo.text", ArgTypes.STRING);
    private final RequiredArg<Integer> valueArg =
            this.withRequiredArg("value", "myplugin.commands.echo.times", ArgTypes.INTEGER);

    public ExampleCommand(String pluginName) {
        super("test", "Prints a test message from the " + pluginName + " plugin.");
        this.setPermissionGroup(GameMode.Adventure); // Allows the command to be used by anyone, not just OP
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        String name = nameArg.get(ctx);
        Integer value = valueArg.get(ctx);

        ctx.sendMessage(Message.raw(
                name + " blabla value " + value));
    }
}