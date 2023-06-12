package com.github.allinkdev.deviousmod.api.module.settings;

import java.io.IOException;
import java.util.Set;

/**
 * A class that handles dynamic settings for a module.
 */
public interface ModuleSettings {
    /**
     * Reads the value represented by the setting key
     *
     * @param name  the name of the value
     * @param clazz the class of the value
     * @param <T>   the value type
     * @return the read value
     */
    <T> T readValue(final String name, final Class<T> clazz);

    /**
     * Writes a new value to the setting key
     *
     * @param name   the name of the value
     * @param object the new value
     * @param clazz  the class of the value
     * @param <T>    the value type
     * @throws IOException if the changes failed to be saved to disk
     */
    <T> void writeValue(final String name, final T object, final Class<T> clazz) throws IOException;

    /**
     * Returns a set of all registered keys
     *
     * @return an unmodifiable set containing all the registered keys
     */
    Set<String> getKeys();
}
