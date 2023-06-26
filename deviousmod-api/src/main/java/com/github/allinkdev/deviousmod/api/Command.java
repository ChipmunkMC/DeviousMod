package com.github.allinkdev.deviousmod.api;

import com.github.allinkdev.deviousmod.api.experiments.Experimentable;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

/**
 * A class that implements {@link com.mojang.brigadier.Command<T>} and stores metadata about the command
 *
 * @param <T> the command source
 */
public abstract class Command<T> implements com.mojang.brigadier.Command<T>, Experimentable {
    /**
     * @return the name of the command object
     */
    public abstract String getCommandName();

    /**
     * @return the command object represented as a brigadier {@link LiteralArgumentBuilder<T>}
     */
    public abstract LiteralArgumentBuilder<T> getNode();

    /**
     * @return the command source type associated with the command object
     */
    public abstract Class<? extends T> getSourceType();

    /**
     * Registers the command object to the provided {@link CommandDispatcher<T>}
     *
     * @param dispatcher the {@link CommandDispatcher<T>} to register the command object to
     */
    public abstract void register(final CommandDispatcher<T> dispatcher);
}
