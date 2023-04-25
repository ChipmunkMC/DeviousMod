package me.allinkdev.deviousmod.gui;

import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import me.allinkdev.deviousmod.util.NoConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ImGuiHolder extends NoConstructor {
    private static final List<AbstractImGuiLayer> LAYERS = Collections.synchronizedList(new ArrayList<>());
    private static ImGuiImplGl3 imGuiImplGl3;
    private static ImGuiImplGlfw imGuiImplGlfw;

    public static void create() {
        imGuiImplGlfw = new ImGuiImplGlfw();
        imGuiImplGl3 = new ImGuiImplGl3();
    }

    private static void process(final AbstractImGuiLayer layer) {
        final List<AbstractImGuiLayer> children = layer.getChildren();

        layer.preProcess();
        layer.process();
        layer.postProcess();

        children.forEach(ImGuiHolder::process);
    }

    public static void render() {
        LAYERS.forEach(ImGuiHolder::process);
    }

    public static void addLayer(final AbstractImGuiLayer layer) {
        layer.init();

        LAYERS.add(layer);
    }

    public static void removeLayer(final AbstractImGuiLayer layer) {
        LAYERS.remove(layer);
    }

    public static ImGuiImplGl3 getImGuiImplGl3() {
        return imGuiImplGl3;
    }

    public static ImGuiImplGlfw getImGuiImplGlfw() {
        return imGuiImplGlfw;
    }
}
