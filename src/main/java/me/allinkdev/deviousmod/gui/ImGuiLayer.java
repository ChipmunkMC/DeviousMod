package me.allinkdev.deviousmod.gui;

public interface ImGuiLayer {
    default void preProcess() {

    }

    default void init() {

    }

    void process();

    default void postProcess() {

    }
}
