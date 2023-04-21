package me.allinkdev.deviousmod.mixin.patches.text;

import io.netty.buffer.AbstractByteBuf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractByteBuf.class, remap = false)
public final class TellKickFix {
    @Shadow
    @Final
    private static boolean checkBounds;
    @Shadow
    private int writerIndex;
    @Shadow
    private int readerIndex;

    @Inject(method = "readByte", at = @At("HEAD"), cancellable = true)
    private void onPreReadByte(final CallbackInfoReturnable<Byte> cir) {
        if (checkBounds && readerIndex > writerIndex - 1) {
            cir.cancel();
            cir.setReturnValue((byte) 0);
        }
    }
}
