package com.github.allinkdev.deviousmod.api.managers;

import com.github.allinkdev.deviousmod.api.module.Module;

import java.util.Optional;
import java.util.Set;

/**
 * Class for storing &amp; loading modules
 */
public interface ModuleManager {
    /**
     * @return a set containing the names of every {@link Module} object registered with this module manager object.
     */
    Set<String> getModuleNames();

    /**
     * Enables listeners for the provided {@link Module} object.
     *
     * @param module the module object to enable listeners for
     */
    void load(final Module module);

    /**
     * Disables listeners for the provided {@link Module} object.
     *
     * @param module the module object to disable listeners for
     */
    void unload(final Module module);

    /**
     * @return an unmodifiable set containing every registered {@link Module} object.
     */
    Set<Module> getModules();

    /**
     * Finds a {@link Module} object by name while ignoring case.
     *
     * @param name the module name, as a {@link CharSequence} to search for
     * @return an {@link Optional<Module>} that could contain the found module
     */
    Optional<Module> findModule(final CharSequence name);
}
