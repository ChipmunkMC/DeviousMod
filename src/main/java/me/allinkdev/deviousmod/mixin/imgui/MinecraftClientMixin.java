package me.allinkdev.deviousmod.mixin.imgui;

import com.github.allinkdev.deviousmod.api.gui.ImGuiHolder;
import me.allinkdev.deviousmod.DeviousMod;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public final class MinecraftClientMixin {
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;endWrite()V"))
    private void onRender(final boolean bl, final CallbackInfo ci) {
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final ImGuiHolder imGuiHolder = deviousMod.getImGuiHolder();
        imGuiHolder.newFrame();
    }
}
