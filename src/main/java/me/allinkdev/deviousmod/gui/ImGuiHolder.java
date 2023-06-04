package me.allinkdev.deviousmod.gui;

import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import me.allinkdev.deviousmod.util.NoConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ImGuiHolder extends NoConstructor {
    private static final List<ImGuiLayer> LAYERS = Collections.synchronizedList(new ArrayList<>());
    private static ImGuiImplGl3 imGuiImplGl3;
    private static ImGuiImplGlfw imGuiImplGlfw;

    public static void create() {
        imGuiImplGlfw = new ImGuiImplGlfw();
        imGuiImplGl3 = new ImGuiImplGl3();
    }

    private static void process(final ImGuiLayer layer) {
        final List<ImGuiLayer> children = new ArrayList<>();

        if (layer instanceof final AbstractImGuiLayer abstractImGuiLayer) {
            final List<ImGuiLayer> abstractChildren = abstractImGuiLayer.getChildren();
            children.addAll(abstractChildren);
        }

        layer.preProcess();
        layer.process();
        layer.postProcess();

        children.forEach(ImGuiHolder::process);
    }

    public static void render() {
        LAYERS.forEach(ImGuiHolder::process);
    }

    public static void addLayer(final ImGuiLayer layer) {
        layer.layerInit();

        LAYERS.add(layer);
    }

    public static void removeLayer(final ImGuiLayer layer) {
        LAYERS.remove(layer);
    }

    public static ImGuiImplGl3 getImGuiImplGl3() {
        return imGuiImplGl3;
    }

    public static ImGuiImplGlfw getImGuiImplGlfw() {
        return imGuiImplGlfw;
    }
}
