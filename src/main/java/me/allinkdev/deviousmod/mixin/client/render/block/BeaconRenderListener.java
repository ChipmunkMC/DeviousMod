package me.allinkdev.deviousmod.mixin.client.render.block;

import com.github.allinkdev.deviousmod.api.managers.EventManager;
import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.render.block.PreBeaconBeamRenderEvent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconBlockEntityRenderer.class)
public final class BeaconRenderListener {
    @Inject(method = "renderBeam(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/util/Identifier;FFJII[FFF)V", at = @At("HEAD"), cancellable = true)
    private static void onRenderBeam(final MatrixStack matrices, final VertexConsumerProvider vertexConsumers,
                                     final Identifier textureId,
                                     final float tickDelta, final float heightScale,
                                     final long worldTime,
                                     final int yOffset, final int maxY,
                                     final float[] color,
                                     final float innerRadius, final float outerRadius,
                                     final CallbackInfo ci) {
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventManager<EventBus> eventManager = deviousMod.getEventManager();
        final PreBeaconBeamRenderEvent event = new PreBeaconBeamRenderEvent();
        eventManager.broadcastEvent(event);

        if (!event.isCancelled()) {
            return;
        }

        ci.cancel();
    }
}
