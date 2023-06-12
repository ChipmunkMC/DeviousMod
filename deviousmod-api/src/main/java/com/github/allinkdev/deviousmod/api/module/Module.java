package com.github.allinkdev.deviousmod.api.module;

import com.github.allinkdev.deviousmod.api.lifecycle.LifecycleTracker;
import com.github.allinkdev.deviousmod.api.module.settings.ModuleSettings;

/**
 * Class containing metadata &amp; state information and logic pertaining to the modification of the game.
 */
public interface Module extends LifecycleTracker<ModuleLifecycle> {
    /**
     * @return the name of the module object
     */
    String getModuleName();

    /**
     * @return a summary of the module object's function.
     */
    String getDescription();

    /**
     * <strong>Note for dependents: There is no "central category registration" functionality in DeviousMod. You will have to implement your own category system.</strong>
     *
     * @return the category of the module object
     */
    String getCategory();

    /**
     * @return the settings for this module
     */
    ModuleSettings getSettings();

    /**
     * @return the toggle state of the module object (enabled: true, disabled: false)
     */
    boolean getModuleState();

    /**
     * Sets the toggle state of the module object to a boolean value.
     *
     * @param newState The new toggle state of the module object
     */
    void setModuleState(final boolean newState);

    /**
     * Initializes the module. Should be called on world initialization &amp; module enable.
     */
    void init();

    /**
     * Inverts the module object's toggle state.
     */
    void toggle();

    /**
     * Always invoked when the module object's toggle state is set to "true".
     */
    default void onEnable() {

    }

    /**
     * Always invoked when the module object's toggle state is set to "false".
     */
    default void onDisable() {

    }
}
