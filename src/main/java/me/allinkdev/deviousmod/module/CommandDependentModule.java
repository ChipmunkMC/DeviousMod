package me.allinkdev.deviousmod.module;

import com.mojang.brigadier.CommandDispatcher;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.command.CommandDispatcherSwapEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionEndEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionStartEvent;
import net.lenni0451.lambdaevents.EventHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;

public abstract class CommandDependentModule extends DModule {
    private final String command;
    protected boolean commandPresent;

    protected CommandDependentModule(final DModuleManager moduleManager, final String command) {
        super(moduleManager);

        this.command = command;
    }

    @Override
    public void onEnable() {
        this.commandPresent = false;

        final ClientPlayNetworkHandler networkHandler = DeviousMod.CLIENT.getNetworkHandler();
        if (networkHandler == null) return;
        this.checkPresence(networkHandler.getCommandDispatcher());
    }

    @EventHandler
    public void onConnectionEnd(final ConnectionEndEvent event) {
        this.commandPresent = false;
    }

    @EventHandler
    public void onConnectionStart(final ConnectionStartEvent event) {
        this.commandPresent = false;
    }

    @Override
    public void onDisable() {
        this.commandPresent = false;
    }

    private void checkPresence(final CommandDispatcher<CommandSource> commandDispatcher) {
        this.commandPresent = commandDispatcher.getRoot().getChild(this.command) != null;
    }

    @EventHandler
    public void onCommandDispatcherSwap(final CommandDispatcherSwapEvent event) {
        this.checkPresence(event.getNewDispatcher());
    }
}
