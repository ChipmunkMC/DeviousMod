package me.allinkdev.deviousmod.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.command.DCommand;
import me.allinkdev.deviousmod.command.args.BotKeyArgumentType;
import me.allinkdev.deviousmod.keying.BotKey;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;

import java.util.Optional;

public final class BotEvalCommand extends DCommand {
    private static final CommandSyntaxException KEY_NOT_FOUND = new SimpleCommandExceptionType(Text.of("Key not found!"))
            .create();
    private static final CommandSyntaxException HASHING_FAILED = new SimpleCommandExceptionType(Text.of("Hashing command failed!"))
            .create();
    private static final CommandSyntaxException INVALID_STATE = new SimpleCommandExceptionType(Text.of("In an invalid state..."))
            .create();

    public BotEvalCommand(final DeviousMod deviousMod) {
        super(deviousMod);
    }

    @Override
    public String getCommandName() {
        return "bval";
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getNode() {
        return ClientCommandManager.literal("bval")
                .then(ClientCommandManager.argument("key", new BotKeyArgumentType())
                        .then(ClientCommandManager.argument("command", StringArgumentType.greedyString())
                                .executes(this)));
    }

    @Override
    public int run(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final BotKey botKey = BotKeyArgumentType.getBotKey(context, "key");

        if (botKey == null) {
            throw KEY_NOT_FOUND;
        }

        final String command = StringArgumentType.getString(context, "command");
        final Optional<String> hashOptional = botKey.encode(command);

        if (hashOptional.isEmpty()) {
            throw HASHING_FAILED;
        }

        final String hash = hashOptional.get();

        final ClientPlayNetworkHandler handler = DeviousMod.CLIENT.getNetworkHandler();

        if (handler == null) {
            throw INVALID_STATE;
        }

        handler.sendChatMessage(command + " " + hash);

        return 1;
    }
}
