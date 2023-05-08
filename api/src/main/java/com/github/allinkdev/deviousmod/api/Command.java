package com.github.allinkdev.deviousmod.api;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

public interface Command<T> extends com.mojang.brigadier.Command<T> {
    /**
     * @return the name of the command object
     */
    String getCommandName();

    /**
     * @return the command object represented as a brigadier {@link LiteralArgumentBuilder<T>}
     */
    LiteralArgumentBuilder<T> getNode();

    /**
     * @return the command source type associated with the command object
     */
    Class<? extends T> getSourceType();

    /**
     * Registers the command object to the provided {@link CommandDispatcher<T>}
     *
     * @param dispatcher the {@link CommandDispatcher<T>} to register the command object to
     */
    void register(final CommandDispatcher<T> dispatcher);
}
