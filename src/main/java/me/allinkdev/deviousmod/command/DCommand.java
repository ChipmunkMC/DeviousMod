package me.allinkdev.deviousmod.command;

import com.github.allinkdev.deviousmod.api.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.module.DModuleManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;

public abstract class DCommand implements Command<FabricClientCommandSource> {
    protected final DeviousMod deviousMod;
    protected final DModuleManager moduleManager;

    protected DCommand(final DeviousMod deviousMod) {
        this.deviousMod = deviousMod;
        this.moduleManager = deviousMod.getModuleManager();
    }

    public abstract String getCommandName();

    public LiteralArgumentBuilder<FabricClientCommandSource> getNode() {
        return ClientCommandManager.literal(this.getCommandName())
                .executes(this);
    }

    @Override
    public void register(final CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(this.getNode());
    }

    @Override
    public Class<? extends FabricClientCommandSource> getSourceType() {
        return FabricClientCommandSource.class;
    }

    public void sendFeedback(final Component component) {
        deviousMod.sendMessage(component);
    }

    @Override
    public int run(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        return 0;
    }
}
