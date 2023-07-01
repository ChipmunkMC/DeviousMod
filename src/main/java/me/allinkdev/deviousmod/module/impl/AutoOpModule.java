package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.command.CommandDispatcherSwapEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import net.minecraft.command.CommandSource;

public final class AutoOpModule extends DModule {
    public AutoOpModule(final DModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    public String getModuleName() {
        return "AutoOp";
    }

    @Override
    public String getDescription() {
        return "Automatically re-ops you when you lose operator status.";
    }

    @Override
    public String getCategory() {
        return "Kaboom";
    }

    @Subscribe
    private void onCommandDispatcherSwap(final CommandDispatcherSwapEvent event) {
        final CommandDispatcher<CommandSource> newDispatcher = event.getNewDispatcher();

        final CommandNode<CommandSource> opNode = newDispatcher.getRoot().getChild("op");
        final CommandNode<CommandSource> deopNode = newDispatcher.getRoot().getChild("deop");

        if (opNode == null || deopNode != null) {
            return;
        }

        DeviousMod.getInstance().getCommandQueueManager().addCommandToFront("minecraft:op @s[type=player]");
    }
}
