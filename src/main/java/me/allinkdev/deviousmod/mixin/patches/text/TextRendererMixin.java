package me.allinkdev.deviousmod.mixin.patches.text;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.OrderedText;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(TextRenderer.class)
public abstract class TextRendererMixin {
    @Inject(method = "drawInternal(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;IIZ)I", at = @At("HEAD"), cancellable = true)
    private void onDrawInternal(final String text, final float x, final float y, final int color, final boolean shadow, final Matrix4f matrix, final VertexConsumerProvider vertexConsumers, final TextRenderer.TextLayerType layerType, final int backgroundColor, final int light, final boolean mirror, final CallbackInfoReturnable<Integer> cir) {
        final int length = text.length();

        if (length >= 512) cir.cancel();
    }

    @Inject(method = "drawInternal(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I", at = @At("HEAD"), cancellable = true)
    private void onDrawInternal(final OrderedText text, final float x, final float y, final int color, final boolean shadow, final Matrix4f matrix, final VertexConsumerProvider vertexConsumerProvider, final TextRenderer.TextLayerType layerType, final int backgroundColor, final int light, final CallbackInfoReturnable<Integer> cir) {
        final AtomicInteger atomicLength = new AtomicInteger();

        final CharacterVisitor characterVisitor = (index, style, codePoint) -> {
            atomicLength.getAndIncrement();
            return true;
        };

        text.accept(characterVisitor);

        final int length = atomicLength.get();
        if (length >= 512) cir.cancel();
    }
}
