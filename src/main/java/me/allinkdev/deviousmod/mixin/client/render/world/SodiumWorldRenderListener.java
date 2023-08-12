package me.allinkdev.deviousmod.mixin.client.render.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.allinkdev.deviousmod.event.render.block.PreBlockEntityRenderEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = SodiumWorldRenderer.class, remap = false)
public final class SodiumWorldRenderListener {
    @WrapOperation(method = "renderTileEntities", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V"))
    private <E extends BlockEntity> void onGetVisibleBlockEntities(final BlockEntityRenderDispatcher instance, final E blockEntity, final float tickDelta,
                                                                   final MatrixStack matrices, final VertexConsumerProvider vertexConsumers, final Operation<Void> operation) {
        if (EventUtil.postCancellable(new PreBlockEntityRenderEvent(blockEntity))) return;
        operation.call(instance, blockEntity, tickDelta, matrices, vertexConsumers);
    }
}
