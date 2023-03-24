package me.allinkdev.deviousmod.command.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.command.DCommand;
import me.allinkdev.deviousmod.command.args.BotKeyArgumentType;
import me.allinkdev.deviousmod.command.args.CharsetArgumentType;
import me.allinkdev.deviousmod.command.args.DigestArgumentType;
import me.allinkdev.deviousmod.command.args.KeyTemplateArgumentType;
import me.allinkdev.deviousmod.keying.BotKey;
import me.allinkdev.deviousmod.keying.BotKeyProvider;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.text.Text;

import java.nio.charset.Charset;

public final class KeyringCommand extends DCommand {
    private static final CommandSyntaxException KEY_NOT_FOUND = new SimpleCommandExceptionType(Text.of("Key not found!"))
            .create();

    public KeyringCommand(final DeviousMod deviousMod) {
        super(deviousMod);
    }

    @Override
    public String getCommandName() {
        return "keyring";
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getNode() {
        return ClientCommandManager.literal("keyring")
                .then(ClientCommandManager.literal("add")
                        .then(ClientCommandManager.argument("identifier", StringArgumentType.string())
                                .then(ClientCommandManager.argument("algorithm", new DigestArgumentType())
                                        .then(ClientCommandManager.argument("template", new KeyTemplateArgumentType())
                                                .then(ClientCommandManager.argument("charset", new CharsetArgumentType())
                                                        .then(ClientCommandManager.argument("hashLength", IntegerArgumentType.integer())
                                                                .then(ClientCommandManager.argument("forgivenessSeconds", IntegerArgumentType.integer())
                                                                        .then(ClientCommandManager.argument("key", StringArgumentType.greedyString())
                                                                                .executes(this::add)))))))))
                .then(ClientCommandManager.literal("remove")
                        .then(ClientCommandManager.argument("keyIdentifier", new BotKeyArgumentType())
                                .executes(this::remove)));
    }

    public int remove(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final BotKeyProvider botKeyProvider = deviousMod.getBotKeyProvider();
        final BotKey botKey = BotKeyArgumentType.getBotKey(context, "keyIdentifier");

        if (botKey == null) {
            throw KEY_NOT_FOUND;
        }

        botKeyProvider.removeKey(botKey);
        sendFeedback(context, Component.text("Removed key \"", NamedTextColor.GREEN)
                .append(Component.text(botKey.getIdentifier(), NamedTextColor.GREEN))
                .append(Component.text("\" from the keyring.", NamedTextColor.GREEN)));
        return 1;
    }

    public int add(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final String identifier = StringArgumentType.getString(context, "identifier");
        final String digestType = StringArgumentType.getString(context, "algorithm");
        final String template = StringArgumentType.getString(context, "template");
        final int hashLen = IntegerArgumentType.getInteger(context, "hashLength");
        final int forgivenessSeconds = IntegerArgumentType.getInteger(context, "forgivenessSeconds");
        final String key = StringArgumentType.getString(context, "key");
        final Charset charset = CharsetArgumentType.getCharset(context, "charset");

        final BotKey botKey = BotKey.builder()
                .identifier(identifier)
                .algorithm(digestType)
                .template(template)
                .hashLen(hashLen)
                .timestampForgiveness(forgivenessSeconds)
                .key(key)
                .charset(charset)
                .build();

        final BotKeyProvider botKeyProvider = deviousMod.getBotKeyProvider();
        botKeyProvider.addKey(botKey);

        sendFeedback(context, Component.text("Added key \"", NamedTextColor.GREEN)
                .append(Component.text(identifier, NamedTextColor.GREEN))
                .append(Component.text("\" to the keyring.", NamedTextColor.GREEN)));

        return 1;
    }

    @Override
    public int execute(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        return 0;
    }
}
