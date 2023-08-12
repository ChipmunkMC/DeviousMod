package com.github.allinkdev.deviousmod.api.event.impl.module;

import com.github.allinkdev.deviousmod.api.event.Event;
import com.github.allinkdev.deviousmod.api.managers.ModuleManager;
import com.github.allinkdev.deviousmod.api.module.Module;

import java.util.function.Function;

/**
 * Fired when a DeviousMod ModuleManager initializes.
 */
public interface ModuleManagerInitializeEvent extends Event {
    /**
     * Adds the provided {@link Module} object to the {@link ModuleManager}.
     *
     * @param moduleCreationMethod a function that when supplied a {@link ModuleManager} returns an instance of the module
     */
    void addModule(final Function<ModuleManager, Module> moduleCreationMethod);
}
