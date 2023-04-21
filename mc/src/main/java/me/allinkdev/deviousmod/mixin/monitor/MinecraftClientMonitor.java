package me.allinkdev.deviousmod.mixin.monitor;

import me.allinkdev.deviousmod.thread.DeviousPuppy;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public final class MinecraftClientMonitor {
    @Inject(method = "run", at = @At(value = "HEAD"))
    private void onRun(final CallbackInfo ci) {
        DeviousPuppy.INSTANCE.monitorMe();
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    private void onTick(final CallbackInfo ci) {
        DeviousPuppy.INSTANCE.contactMyThreadMonitor();
    }
}
