package com.github.allinkdev.deviousmod.api.module.settings;

import com.github.allinkdev.deviousmod.api.module.Module;
import imgui.ImGui;

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

    /**
     * Renders the setting for the ClickGUI
     *
     * @param module   the module this setting is for
     * @param settings the settings for the module
     */
    default void render(final Module module, final ModuleSettings settings) {

    }

    /**
     * Renders the setting description for the ClickGUI.
     * Only called when the ImGUI item is being hovered on.
     *
     * @param module   the module this setting is for
     * @param settings the settings for the module
     */
    default void renderDescription(final Module module, final ModuleSettings settings) {
        if (this.getDescription() != null) {
            ImGui.beginTooltip();
            ImGui.text(this.getDescription());
            ImGui.endTooltip();
        }
    }
}
