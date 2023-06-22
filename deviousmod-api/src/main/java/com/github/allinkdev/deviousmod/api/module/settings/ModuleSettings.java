package com.github.allinkdev.deviousmod.api.module.settings;

import java.io.IOException;
import java.util.Set;

/**
 * A class that handles dynamic settings for a module.
 */
public interface ModuleSettings {
    /**
     * Reads a setting
     *
     * @param name  the name of the value
     * @param clazz the class of the value
     * @param <T>   the value type
     * @return the read value
     */
    <T> Setting<T> getSetting(final String name, final Class<T> clazz);

    /**
     * Writes a new value to the setting
     *
     * @param name   the name of the value
     * @param object the new value
     * @param clazz  the class of the value
     * @param <T>    the value type
     * @throws IOException if the changes failed to be saved to disk
     */
    <T> void writeValue(final String name, final T object, final Class<T> clazz) throws IOException;

    /**
     * Gets the class of a value
     *
     * @param name the name of the value to query the class of
     * @return the class of the value
     */
    Class<?> getValueClass(final String name);

    /**
     * Returns a set of all registered keys
     *
     * @return an unmodifiable set containing all the registered keys
     */
    Set<String> getKeys();
}
