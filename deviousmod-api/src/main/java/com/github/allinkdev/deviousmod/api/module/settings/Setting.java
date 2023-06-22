package com.github.allinkdev.deviousmod.api.module.settings;

/**
 * Represents a configurable value that affects the behaviour of a module.
 */
public interface Setting<T> {
    /**
     * @return the internal name of the setting
     */
    String getName();

    /**
     * @return the friendly name of the setting
     */
    String getFriendlyName();

    /**
     * @return the description of the setting
     */
    String getDescription();

    /**
     * @return the value of the setting
     */
    T getValue();

    /**
     * @return the class of the setting's value
     */
    Class<T> getValueClass();
}
