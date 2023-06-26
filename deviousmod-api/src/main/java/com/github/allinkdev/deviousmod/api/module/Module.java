package com.github.allinkdev.deviousmod.api.module;

import com.github.allinkdev.deviousmod.api.experiments.Experimentable;
import com.github.allinkdev.deviousmod.api.lifecycle.GenericLifecycleTracker;
import com.github.allinkdev.deviousmod.api.lifecycle.LifecycleTracker;
import com.github.allinkdev.deviousmod.api.module.settings.ModuleSettings;

/**
 * Class containing metadata &amp; state information and logic pertaining to the modification of the game.
 */
public abstract class Module extends GenericLifecycleTracker<ModuleLifecycle> implements LifecycleTracker<ModuleLifecycle>, Experimentable {
    /**
     * Creates a new module.
     */
    protected Module() {
        super(ModuleLifecycle.NONE);
    }

    /**
     * @return the name of the module object
     */
    public abstract String getModuleName();

    /**
     * @return a summary of the module object's function.
     */
    public abstract String getDescription();

    /**
     * <strong>Note for dependents: There is no "central category registration" functionality in DeviousMod. You will have to implement your own category system.</strong>
     *
     * @return the category of the module object
     */
    public abstract String getCategory();

    /**
     * @return the settings for this module
     */
    public abstract ModuleSettings getSettings();

    /**
     * @return the toggle state of the module object (enabled: true, disabled: false)
     */
    public abstract boolean getModuleState();

    /**
     * Sets the toggle state of the module object to a boolean value.
     *
     * @param newState The new toggle state of the module object
     */
    public abstract void setModuleState(final boolean newState);

    /**
     * Initializes the module. Should be called on world initialization &amp; module enable.
     */
    public abstract void init();

    /**
     * Inverts the module object's toggle state.
     */
    public abstract void toggle();

    /**
     * Always invoked when the module object's toggle state is set to "true".
     */
    public void onEnable() {

    }

    /**
     * Always invoked when the module object's toggle state is set to "false".
     */
    public void onDisable() {

    }
}
