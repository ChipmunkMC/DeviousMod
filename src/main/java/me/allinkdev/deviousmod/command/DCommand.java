package me.allinkdev.deviousmod.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.module.ModuleManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.minecraft.command.CommandRegistryAccess;

public abstract class DCommand implements Command<FabricClientCommandSource> {
    protected final DeviousMod deviousMod;
    protected final ModuleManager moduleManager;

    protected DCommand(final DeviousMod deviousMod) {
        this.deviousMod = deviousMod;
        this.moduleManager = deviousMod.getModuleManager();
    }

    public abstract String getCommandName();

    public LiteralArgumentBuilder<FabricClientCommandSource> getNode() {
        return ClientCommandManager.literal(this.getCommandName())
                .executes(this);
    }

    public void register(final CommandDispatcher<FabricClientCommandSource> dispatcher, final CommandRegistryAccess ignoredRegistryAccess) {
        dispatcher.register(this.getNode());
    }

    public void sendFeedback(final Component component) {
        deviousMod.sendMessage(component);
    }
}
