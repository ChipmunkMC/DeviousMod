package me.allinkdev.deviousmod.gui;

public interface ImGuiLayer {
    default void preProcess() {

    }

    default void layerInit() {

    }

    void process();

    default void postProcess() {

    }
}
