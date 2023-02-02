package me.allinkdev.deviousmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.allinkdev.deviousmod.util.TextUtil;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

public interface DCommand {
    String getCommandName();

    default LiteralArgumentBuilder<FabricClientCommandSource> getNode() {
        return LiteralArgumentBuilder.<FabricClientCommandSource>literal(this.getCommandName())
                .executes(this::execute);
    }

    default void register(final CommandDispatcher<FabricClientCommandSource> dispatcher, final CommandRegistryAccess ignoredRegistryAccess) {
        dispatcher.register(this.getNode());
    }

    default void sendFeedback(final CommandContext<FabricClientCommandSource> context, final Component component) {
        final FabricClientCommandSource source = context.getSource();
        final Text text = TextUtil.toText(component);

        source.sendFeedback(text);
    }

    int execute(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException;
}
