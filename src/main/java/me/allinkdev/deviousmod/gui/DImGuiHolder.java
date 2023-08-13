package me.allinkdev.deviousmod.gui;

import com.github.allinkdev.deviousmod.api.gui.ImGuiHolder;
import com.github.allinkdev.deviousmod.api.gui.ImGuiLayer;
import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DImGuiHolder implements ImGuiHolder {
    private final List<ImGuiLayer> layers = Collections.synchronizedList(new ArrayList<>());
    private ImGuiImplGl3 imGuiImplGl3;
    private ImGuiImplGlfw imGuiImplGlfw;

    public DImGuiHolder(final long handle) {
        this.imGuiImplGl3 = new ImGuiImplGl3();
        this.imGuiImplGlfw = new ImGuiImplGlfw();

        ImGui.createContext();

        final ImGuiIO io = ImGui.getIO();
        final ImFontConfig fontConfig = new ImFontConfig();
        final ImFontAtlas fontAtlas = io.getFonts();

        final ClassLoader classLoader = this.getClass().getClassLoader();
        final InputStream resourceStream = classLoader.getResourceAsStream("assets/deviousmod/Roboto.ttf");

        if (resourceStream == null) {
            throw new IllegalStateException("Roboto not found!");
        }

        try {
            fontAtlas.addFontFromMemoryTTF(resourceStream.readAllBytes(), 16, fontConfig);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load font", e);
        }

        try {
            resourceStream.close();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to close resource stream", e);
        }

        fontConfig.destroy();
        this.imGuiImplGlfw.init(handle, true);
        this.imGuiImplGl3.init();
    }

    public ImGuiImplGl3 getImGuiImplGl3() {
        return this.imGuiImplGl3;
    }

    public ImGuiImplGlfw getImGuiImplGlfw() {
        return this.imGuiImplGlfw;
    }

    public void addLayer(final ImGuiLayer layer) {
        layer.layerInit();
        this.layers.add(layer);
    }

    public void removeLayer(final ImGuiLayer layer) {
        this.layers.remove(layer);
    }

    @Override
    public void newFrame() {
        this.imGuiImplGlfw.newFrame();
        ImGui.newFrame();
        this.render();
        ImGui.render();
        this.imGuiImplGl3.renderDrawData(ImGui.getDrawData());
    }

    @Override
    public void handleScrollCallback(final long id, final double xO, final double yO) {
        this.imGuiImplGlfw.scrollCallback(id, xO, yO);
    }

    public void create() {
        this.imGuiImplGlfw = new ImGuiImplGlfw();
        this.imGuiImplGl3 = new ImGuiImplGl3();
    }

    public void process(final ImGuiLayer layer) {
        final List<ImGuiLayer> children = layer.getChildren();
        layer.preProcess();
        layer.process();
        layer.postProcess();

        children.forEach(this::process);
    }

    public void render() {
        this.layers.forEach(this::process);
    }
}
