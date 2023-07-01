package com.github.allinkdev.deviousmod.api.event.impl.imgui;

import com.github.allinkdev.deviousmod.api.event.Event;
import com.github.allinkdev.deviousmod.api.gui.ImGuiHolder;

import java.util.Optional;

/**
 * Allows external clients to override the DeviousMod ImGuiHolder with their own. This is required for all other mods that use ImGui to implement.
 */
public class ImGuiHolderOverrideEvent implements Event {
    private ImGuiHolder imGuiHolder;

    /**
     * Gets the overridden ImGuiHolder instance.
     *
     * @return the overridden ImGuiHolder, empty if not overridden
     */
    public Optional<ImGuiHolder> getImGuiHolder() {
        return Optional.ofNullable(this.imGuiHolder);
    }

    /**
     * Sets the overridden ImGuiHolder
     *
     * @param imGuiHolder the ImGui holder to override with
     */
    public void setImGuiHolder(final ImGuiHolder imGuiHolder) {
        this.imGuiHolder = imGuiHolder;
    }
}
