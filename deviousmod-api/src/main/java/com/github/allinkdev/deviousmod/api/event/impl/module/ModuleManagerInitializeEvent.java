package com.github.allinkdev.deviousmod.api.event.impl.module;

import com.github.allinkdev.deviousmod.api.event.Event;
import com.github.allinkdev.deviousmod.api.module.Module;

/**
 * Fired when a DeviousMod ModuleManager initializes.
 */
public interface ModuleManagerInitializeEvent extends Event {
    /**
     * Adds the provided {@link Module} object to the ModuleManager.
     *
     * @param module the module object to add to the ModuleManager
     */
    void addModule(final Module module);
}
