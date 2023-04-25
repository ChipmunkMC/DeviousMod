package me.allinkdev.deviousmod.mixin.imgui;

import imgui.ImDrawData;
import imgui.ImGui;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import me.allinkdev.deviousmod.gui.ImGuiHolder;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public final class MinecraftClientMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;endWrite()V"))
    private void onRender(final boolean bl, final CallbackInfo ci) {
        final ImGuiImplGlfw imGuiImplGlfw = ImGuiHolder.getImGuiImplGlfw();

        imGuiImplGlfw.newFrame();
        ImGui.newFrame();

        ImGuiHolder.render();

        ImGui.render();

        final ImGuiImplGl3 imGuiImplGl3 = ImGuiHolder.getImGuiImplGl3();
        final ImDrawData drawData = ImGui.getDrawData();

        imGuiImplGl3.renderDrawData(drawData);
    }
}
