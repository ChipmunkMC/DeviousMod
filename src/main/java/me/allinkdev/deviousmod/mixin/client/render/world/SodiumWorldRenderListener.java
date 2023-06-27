package me.allinkdev.deviousmod.mixin.client.render.world;

import me.allinkdev.deviousmod.event.render.block.PreBlockEntityRenderEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "me/jellysquid/mods/sodium/client/render/SodiumWorldRenderer")
public final class SodiumWorldRenderListener {
    @Redirect(method = "renderTileEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V"))
    private <E extends BlockEntity> void onGetVisibleBlockEntities(final BlockEntityRenderDispatcher instance, final E blockEntity, final float tickDelta,
                                                                   final MatrixStack matrices, final VertexConsumerProvider vertexConsumers) {
        if (EventUtil.postCancellable(new PreBlockEntityRenderEvent(blockEntity))) {
            return;
        }

        instance.render(blockEntity, tickDelta, matrices, vertexConsumers);
    }
}
