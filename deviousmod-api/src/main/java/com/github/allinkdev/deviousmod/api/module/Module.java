package com.github.allinkdev.deviousmod.api.module;

import com.github.allinkdev.deviousmod.api.experiments.Experimentable;
import com.github.allinkdev.deviousmod.api.lifecycle.GenericLifecycleTracker;
import com.github.allinkdev.deviousmod.api.lifecycle.LifecycleTracker;
import com.github.allinkdev.deviousmod.api.managers.ModuleManager;
import com.github.allinkdev.deviousmod.api.module.settings.ModuleSettings;

import java.nio.file.Paths;

/**
 * Class containing metadata &amp; state information and logic pertaining to the modification of the game.
 */
public abstract class Module extends GenericLifecycleTracker<ModuleLifecycle> implements LifecycleTracker<ModuleLifecycle>, Experimentable {
    /**
     * The tied {@link ModuleManager} instance
     */
    protected final ModuleManager moduleManager;
    /**
     * The settings of this module
     */
    protected final ModuleSettings settings;

    /**
     * Creates a new module.
     *
     * @param moduleManager The module manager that is handling this {@link Module}
     */
    protected Module(final ModuleManager moduleManager) {
        super(ModuleLifecycle.NONE);
        this.moduleManager = moduleManager;
        this.settings = this.getSettingsBuilder().build();
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
     * @return a builder for the settings of this module
     */
    protected ModuleSettings.Builder getSettingsBuilder() {
        return this.moduleManager.getModuleSettingsBuilderSupplier().get()
                .setPath(Paths.get("modules", this.getModuleName().toLowerCase() + ".json"))
                .addField("enabled", "", "", false);
    }

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
     * Notifies the client about changes to this module's state.
     *
     * @param newState The new toggle state of the module object
     */
    public abstract void notifyModuleStateUpdate(final boolean newState);

    /**
     * Initializes the module. Should be called on world initialization &amp; module enable.
     */
    public abstract void init();

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
