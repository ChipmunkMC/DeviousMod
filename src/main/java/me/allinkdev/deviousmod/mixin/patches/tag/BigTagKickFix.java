package me.allinkdev.deviousmod.mixin.patches.tag;

import net.minecraft.nbt.NbtTagSizeTracker;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NbtTagSizeTracker.class)
public final class BigTagKickFix {
    @Shadow
    private long allocatedBytes;

    @Shadow
    @Final
    private long maxBytes;

    @Redirect(method = "add", at = @At(value = "FIELD", target = "Lnet/minecraft/nbt/NbtTagSizeTracker;allocatedBytes:J", opcode = Opcodes.PUTFIELD))
    private void onAdd(final NbtTagSizeTracker instance, final long value) {
        this.allocatedBytes = Math.min(this.allocatedBytes + 1, this.maxBytes);
    }
}
