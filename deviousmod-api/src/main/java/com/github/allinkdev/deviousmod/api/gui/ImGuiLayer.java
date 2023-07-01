package com.github.allinkdev.deviousmod.api.gui;

import java.util.Collections;
import java.util.List;

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

    /**
     * Adds a child to this layer.
     *
     * @param imGuiLayer the child to add
     */
    default void addChild(final ImGuiLayer imGuiLayer) {
        //
    }

    /**
     * @return the children of this {@link ImGuiLayer}
     */
    default List<ImGuiLayer> getChildren() {
        return Collections.emptyList();
    }
}
