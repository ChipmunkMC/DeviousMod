package me.allinkdev.deviousmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

public abstract class DCommand {
    protected DCommand() { }

    public abstract String getCommandName();

    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess ignoredRegistryAccess) {
        dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal(this.getCommandName()).executes(this::execute));
    }

    public abstract int execute(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException;
}
