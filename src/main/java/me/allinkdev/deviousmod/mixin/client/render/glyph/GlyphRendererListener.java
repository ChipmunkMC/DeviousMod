package me.allinkdev.deviousmod.mixin.client.render.glyph;

import me.allinkdev.deviousmod.event.render.glyph.PreGlyphRenderEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.client.font.GlyphRenderer;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GlyphRenderer.class)
public final class GlyphRendererListener {
    @Inject(method = "draw", at = @At("HEAD"), cancellable = true)
    private void onDraw(final boolean italic, final float x, final float y, final Matrix4f matrix, final VertexConsumer vertexConsumer,
                        final float red, final float green, final float blue, final float alpha, final int light, final CallbackInfo ci) {
        if (EventUtil.postCancellable(new PreGlyphRenderEvent())) ci.cancel();
    }
}
