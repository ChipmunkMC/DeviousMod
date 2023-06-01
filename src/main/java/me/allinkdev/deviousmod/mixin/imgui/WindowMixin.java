package me.allinkdev.deviousmod.mixin.imgui;

import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import me.allinkdev.deviousmod.gui.ImGuiHolder;
import net.minecraft.client.WindowEventHandler;
import net.minecraft.client.WindowSettings;
import net.minecraft.client.util.MonitorTracker;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.io.InputStream;

@Mixin(Window.class)
public final class WindowMixin {
    @Shadow
    @Final
    private long handle;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void onInit(final WindowEventHandler windowEventHandler, final MonitorTracker monitorTracker, final WindowSettings windowSettings, final String string, final String string2, final CallbackInfo ci) {
        ImGuiHolder.create();

        final ImGuiImplGlfw imGuiImplGlfw = ImGuiHolder.getImGuiImplGlfw();
        final ImGuiImplGl3 imGuiImplGl3 = ImGuiHolder.getImGuiImplGl3();

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

        imGuiImplGlfw.init(handle, true);
        imGuiImplGl3.init();
    }

    @Inject(method = "close", at = @At(value = "TAIL"))
    private void onClose(final CallbackInfo ci) {
        final ImGuiImplGlfw imGuiImplGlfw = ImGuiHolder.getImGuiImplGlfw();
        final ImGuiImplGl3 imGuiImplGl3 = ImGuiHolder.getImGuiImplGl3();

        imGuiImplGlfw.dispose();
        imGuiImplGl3.dispose();
    }
}
