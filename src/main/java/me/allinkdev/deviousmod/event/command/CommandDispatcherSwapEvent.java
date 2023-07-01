package me.allinkdev.deviousmod.event.command;

import com.github.allinkdev.deviousmod.api.event.Event;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;

public final class CommandDispatcherSwapEvent implements Event {
    private final CommandDispatcher<CommandSource> previousDispatcher;
    private final CommandDispatcher<CommandSource> newDispatcher;

    public CommandDispatcherSwapEvent(final CommandDispatcher<CommandSource> previousDispatcher, final CommandDispatcher<CommandSource> newDispatcher) {
        this.previousDispatcher = previousDispatcher;
        this.newDispatcher = newDispatcher;
    }

    public CommandDispatcher<CommandSource> getNewDispatcher() {
        return this.newDispatcher;
    }

    public CommandDispatcher<CommandSource> getPreviousDispatcher() {
        return this.previousDispatcher;
    }
}
