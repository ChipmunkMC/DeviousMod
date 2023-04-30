package me.allinkdev.deviousmod.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.command.DCommand;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.command.argument.NbtElementArgumentType;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;

public final class PrettyNBTCommand extends DCommand {
    private static final GsonComponentSerializer GSON_COMPONENT_SERIALIZER = GsonComponentSerializer.gson();

    public PrettyNBTCommand(final DeviousMod deviousMod) {
        super(deviousMod);
    }

    @Override
    public String getCommandName() {
        return "prettynbt";
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getNode() {
        return ClientCommandManager.literal("prettynbt")
                .then(ClientCommandManager.argument("nbt", NbtElementArgumentType.nbtElement())
                        .executes(this));
    }

    @Override
    public int run(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final NbtElement nbtElement = NbtElementArgumentType.getNbtElement(context, "nbt");
        final Text prettyPrinted = NbtHelper.toPrettyPrintedText(nbtElement);
        final Component prettyPrintedComponent = prettyPrinted.asComponent();
        final String prettyPrintedJson = GSON_COMPONENT_SERIALIZER.serialize(prettyPrintedComponent);
        final Component feedback = Component.text("Pretty printed NBT: ", NamedTextColor.GRAY)
                .append(Component.text(prettyPrintedJson, NamedTextColor.WHITE)
                        .clickEvent(ClickEvent.copyToClipboard(prettyPrintedJson)));

        this.sendFeedback(feedback);
        return 1;
    }
}
