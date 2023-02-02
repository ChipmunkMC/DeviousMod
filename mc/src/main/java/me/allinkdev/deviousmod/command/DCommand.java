package me.allinkdev.deviousmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public interface DCommand {
    String getCommandName();

    default LiteralArgumentBuilder<FabricClientCommandSource> getNode() {
        return LiteralArgumentBuilder.<FabricClientCommandSource>literal(this.getCommandName())
                .executes(this::execute);
    }

    default void register(final CommandDispatcher<FabricClientCommandSource> dispatcher, final CommandRegistryAccess ignoredRegistryAccess) {
        dispatcher.register(this.getNode());
    }

    int execute(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException;
}
