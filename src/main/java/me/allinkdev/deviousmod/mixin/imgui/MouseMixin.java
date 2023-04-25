package me.allinkdev.deviousmod.mixin.imgui;

import imgui.glfw.ImGuiImplGlfw;
import me.allinkdev.deviousmod.gui.ImGuiHolder;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public final class MouseMixin {
    @Inject(method = "onMouseScroll", at = @At(value = "HEAD"))
    private void onMouseScroll(final long id, final double xO, final double yO, final CallbackInfo ci) {
        final ImGuiImplGlfw glfw = ImGuiHolder.getImGuiImplGlfw();

        glfw.scrollCallback(id, xO, yO);
    }
}
