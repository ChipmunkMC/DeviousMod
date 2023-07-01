package me.allinkdev.deviousmod.mixin.imgui;

import me.allinkdev.deviousmod.DeviousMod;
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

@Mixin(Window.class)
public final class WindowMixin {
    @Shadow
    @Final
    private long handle;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void onInit(final WindowEventHandler windowEventHandler, final MonitorTracker monitorTracker, final WindowSettings windowSettings, final String string, final String string2, final CallbackInfo ci) {
        DeviousMod.getInstance().createImGuiHolder(handle);
    }
}
