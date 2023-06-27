package me.allinkdev.deviousmod.mixin.client.render;

import com.github.allinkdev.deviousmod.api.managers.EventManager;
import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.render.block.PreBlockEntityRenderEvent;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "me/jellysquid/mods/sodium/client/render/SodiumWorldRenderer", remap = false)
public final class SodiumWorldRenderListener {
    @Redirect(method = "renderTileEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V"))
    private <E extends BlockEntity> void onGetVisibleBlockEntities(final BlockEntityRenderDispatcher instance, final E blockEntity, final float tickDelta,
                                                                   final MatrixStack matrices, final VertexConsumerProvider vertexConsumers) {
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventManager<EventBus> eventManager = deviousMod.getEventManager();
        final PreBlockEntityRenderEvent event = new PreBlockEntityRenderEvent(blockEntity);
        eventManager.broadcastEvent(event);

        if (event.isCancelled()) {
            return;
        }

        instance.render(blockEntity, tickDelta, matrices, vertexConsumers);
    }
}
