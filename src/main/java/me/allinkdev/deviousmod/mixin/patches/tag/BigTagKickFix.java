package me.allinkdev.deviousmod.mixin.patches.tag;

import net.minecraft.nbt.NbtTagSizeTracker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NbtTagSizeTracker.class)
public final class BigTagKickFix {
    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    private void onAdd(final long bytes, final CallbackInfo ci) {
        ci.cancel();
    }
}
