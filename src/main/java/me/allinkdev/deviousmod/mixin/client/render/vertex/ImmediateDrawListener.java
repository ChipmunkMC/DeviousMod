package me.allinkdev.deviousmod.mixin.client.render.vertex;

import com.github.allinkdev.deviousmod.api.managers.EventManager;
import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.render.entity.RenderLayerEvent;
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
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventManager<EventBus> eventManager = deviousMod.getEventManager();
        final RenderLayerEvent event = new RenderLayerEvent(layer);
        eventManager.broadcastEvent(event);

        if (!event.isCancelled()) {
            return;
        }

        ci.cancel();
    }
}
