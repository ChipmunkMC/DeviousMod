package com.github.allinkdev.deviousmod.api.module;

import com.github.allinkdev.deviousmod.api.lifecycle.Lifecycle;
import com.github.allinkdev.deviousmod.api.managers.ModuleManager;

/**
 * A class that represents the lifecycle {@link Module}
 */
public enum ModuleLifecycle implements Lifecycle {
    /**
     * The module is not registered.
     */
    NONE,
    /**
     * The module has been registered with a {@link ModuleManager}
     */
    REGISTERED,
    /**
     * The module has executed logic before load
     */
    INITIALIZED,
    /**
     * The module might have initialized fields that should be kept across enables and disables
     */
    LOADED,
    /**
     * The module might have cleaned up any fields that were initialized during load, but not needed during unload
     */
    UNLOADED,
    /**
     * The module has been enabled
     */
    ENABLED,
    /**
     * The module has been disabled
     */
    DISABLED;

    @Override
    public Lifecycle getDefault() {
        return NONE;
    }
}
