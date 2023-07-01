package com.github.allinkdev.deviousmod.api.gui;

/**
 * A class that utilizes the ImGui API
 */
public interface ImGuiLayer {
    /**
     * Invoked before the process method.
     */
    default void preProcess() {

    }

    /**
     * Invoked once after the layer is instantiated.
     */
    default void layerInit() {

    }

    /**
     * Invoked when a layer is ready to be rendered.
     */
    void process();

    /**
     * Invoked after a layer has been rendered.
     */
    default void postProcess() {

    }
}
