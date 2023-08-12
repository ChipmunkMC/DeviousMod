package com.github.allinkdev.deviousmod.api.module.settings;

import java.io.IOException;
import java.nio.file.Path;
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

    /**
     * @return whether there are any adjustable settings for this module
     */
    boolean hasSettings();

    /**
     * @return if the path the settings target exists
     */
    boolean doesPathExist();

    /**
     * Creates the directories that house the settings target
     *
     * @throws IOException if the directory creation failed
     */
    void createDirectories() throws IOException;

    /**
     * Saves the settings to disk
     *
     * @throws IOException if saving the settings to disk failed
     */
    void save() throws IOException;

    /**
     * Loads the settings from disk
     *
     * @throws IOException if loading the settings from disk failed
     */
    void load() throws IOException;

    /**
     * A class that provides the dynamic specification of {@link ModuleSettings} objects
     */
    interface Builder {
        /**
         * Adds a field to the builder
         *
         * @param name         the name of the field
         * @param friendlyName the name to show to the user
         * @param description  the description that will be shown to the user
         * @param defaultValue the value the setting will default to if no value override is present
         * @return this builder
         */
        Builder addField(final String name, final String friendlyName, final String description, final Object defaultValue);

        /**
         * Sets the path of the builder
         *
         * @param path the new path
         * @return this builder
         */
        Builder setPath(final Path path);

        /**
         * Builds the builder into a {@link ModuleSettings} implementation
         *
         * @return the built {@link ModuleSettings} instance
         */
        ModuleSettings build();
    }
}
