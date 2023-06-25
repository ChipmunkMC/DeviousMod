package me.allinkdev.deviousmod.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.account.AccountManager;
import me.allinkdev.deviousmod.command.DCommand;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class ClientsideUsernameCommand extends DCommand {
    public ClientsideUsernameCommand(final DeviousMod deviousMod) {
        super(deviousMod);
    }

    @Override
    public String getCommandName() {
        return "cusername";
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getNode() {
        return ClientCommandManager.literal("cusername")
                .then(ClientCommandManager.literal("reset").executes(this::reset))
                .then(ClientCommandManager.literal("set").then(ClientCommandManager.argument("username", StringArgumentType.greedyString()).executes(this::set)));
    }

    private int reset(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final AccountManager accountManager = this.deviousMod.getAccountManager();
        final String originalUsername = accountManager.getOriginalUsername();
        accountManager.restoreSession();

        this.sendFeedback(Component.text("Successfully reverted your session to \"", NamedTextColor.GREEN)
                .append(Component.text(originalUsername, NamedTextColor.GREEN))
                .append(Component.text("\"", NamedTextColor.GREEN)));
        return 1;
    }

    public int set(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final String username = StringArgumentType.getString(context, "username");
        final AccountManager accountManager = this.deviousMod.getAccountManager();

        accountManager.setUsername(username);
        this.sendFeedback(Component.text("Successfully set your username to ", NamedTextColor.GREEN)
                .append(Component.text("\"", NamedTextColor.GREEN))
                .append(Component.text(username, NamedTextColor.GREEN))
                .append(Component.text("\".", NamedTextColor.GREEN))
        );
        return 1;
    }
}
