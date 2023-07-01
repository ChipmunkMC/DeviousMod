package com.github.allinkdev.deviousmod.api.gui;

/**
 * A holder that manages ImGui-related classes
 */
public interface ImGuiHolder {
    /**
     * Processes a layer
     *
     * @param layer the layer to process
     */
    void process(final ImGuiLayer layer);

    /**
     * Renders all currently handled layers.
     */
    void render();

    /**
     * Adds a layer to be rendered.
     *
     * @param layer the layer to be rendered
     */
    void addLayer(final ImGuiLayer layer);

    /**
     * Removes a layer from being rendering
     *
     * @param layer the layer to be removed from rendering
     */
    void removeLayer(final ImGuiLayer layer);

    /**
     * Renders a new frame
     */
    void newFrame();

    /**
     * Handles scroll callbacks
     *
     * @param id the window id
     * @param xO the x delta
     * @param yO the y delta
     */
    void handleScrollCallback(final long id, final double xO, final double yO);
}
