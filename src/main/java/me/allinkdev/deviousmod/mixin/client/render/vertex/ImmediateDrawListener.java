package me.allinkdev.deviousmod.mixin.client.render.vertex;

import me.allinkdev.deviousmod.event.render.entity.RenderLayerEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VertexConsumerProvider.Immediate.class)
public final class ImmediateDrawListener {
    @Inject(method = "draw(Lnet/minecraft/client/render/RenderLayer;)V", at = @At("HEAD"), cancellable = true)
    private void onDrawRenderLayer(final RenderLayer layer, final CallbackInfo ci) {
        if (EventUtil.postCancellable(new RenderLayerEvent(layer))) {
            ci.cancel();
        }
    }
}
