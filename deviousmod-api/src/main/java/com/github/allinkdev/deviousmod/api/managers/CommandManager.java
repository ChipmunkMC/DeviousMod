package com.github.allinkdev.deviousmod.api.managers;

import com.github.allinkdev.deviousmod.api.Command;
import com.mojang.brigadier.CommandDispatcher;

import java.util.Set;

/**
 * Class for storing &amp; registering commands with a {@link CommandDispatcher<T>} instance
 *
 * @param <T> the type of command source {@link Command<T>} objects registered with this {@link CommandManager<T>} utilize
 */
public interface CommandManager<T> {
    /**
     * @return every {@link Command<T>} object registered with this command manager
     */
    Set<Command<T>> getCommands();

    /**
     * Registers every {@link Command<T>} object registered with this command manager to the provided command dispatcher
     *
     * @param dispatcher the command dispatcher to register every command to
     */
    void register(final CommandDispatcher<T> dispatcher);
}
