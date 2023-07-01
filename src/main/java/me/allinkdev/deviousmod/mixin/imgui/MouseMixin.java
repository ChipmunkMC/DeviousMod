package me.allinkdev.deviousmod.mixin.imgui;

import me.allinkdev.deviousmod.DeviousMod;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public final class MouseMixin {
    @Inject(method = "onMouseScroll", at = @At(value = "HEAD"))
    private void onMouseScroll(final long id, final double xO, final double yO, final CallbackInfo ci) {
        DeviousMod.getInstance().getImGuiHolder().handleScrollCallback(id, xO, yO);
    }
}
