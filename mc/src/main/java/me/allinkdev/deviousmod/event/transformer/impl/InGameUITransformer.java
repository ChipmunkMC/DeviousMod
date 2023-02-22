package me.allinkdev.deviousmod.event.transformer.impl;

import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface InGameUITransformer extends Transformer {
    default void preRender(final MatrixStack matrices, final float tickDelta, final CallbackInfo ci) {

    }

    void render(final MatrixStack matrices, final float tickDelta, final CallbackInfo ci);

    default void postRender(final MatrixStack matrixStack, final float tickDelta) {

    }
}
