package me.allinkdev.deviousmod.mixin.client.window;

import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.window.WindowFocusChangeEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Window.class)
public final class WindowFocusListener {
    @Inject(method = "onWindowFocusChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/WindowEventHandler;onWindowFocusChanged(Z)V"))
    private void onWindowFocusChanged(final long window, final boolean focused, final CallbackInfo ci) {
        EventUtil.postEvent(new WindowFocusChangeEvent(DeviousMod.CLIENT.isWindowFocused(), focused));
    }
}
