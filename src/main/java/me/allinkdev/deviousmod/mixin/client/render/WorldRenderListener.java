package me.allinkdev.deviousmod.mixin.client.render;

import com.github.allinkdev.deviousmod.api.event.Cancellable;
import com.github.allinkdev.deviousmod.api.managers.EventManager;
import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.render.block.PreBlockEntityRenderEvent;
import me.allinkdev.deviousmod.event.render.block.PreUncullableBlockEntityRenderEvent;
import me.allinkdev.deviousmod.event.render.entity.EntityRenderPipelineEvent;
import me.allinkdev.deviousmod.event.render.entity.RenderLayerEvent;
import me.allinkdev.deviousmod.mixin.accessor.WorldRendererAccessor;
import me.allinkdev.deviousmod.util.IterUtil;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mixin(WorldRenderer.class)
public final class WorldRenderListener {
    @Shadow
    @Final
    private Set<BlockEntity> noCullingBlockEntities;

    private boolean broadcastCancellable(final Cancellable cancellable) {
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventManager<EventBus> eventManager = deviousMod.getEventManager();
        eventManager.broadcastEvent(cancellable);

        return cancellable.isCancelled();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getEntities()Ljava/lang/Iterable;"))
    private Iterable<Entity> onRender(final ClientWorld instance) {
        final Iterable<Entity> entityIterable = instance.getEntities();
        final List<Entity> entities = IterUtil.toList(entityIterable);

        return this.broadcastCancellable(new EntityRenderPipelineEvent(entities)) ? Collections.emptyList() : entities;
    }

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/WorldRenderer;noCullingBlockEntities:Ljava/util/Set;", opcode = Opcodes.ACC_SYNCHRONIZED))
    private Set<BlockEntity> onGetNoCullingBlockEntities(final WorldRenderer instance) {
        final PreUncullableBlockEntityRenderEvent event = new PreUncullableBlockEntityRenderEvent(this.noCullingBlockEntities);
        final boolean cancelled = this.broadcastCancellable(event);

        return cancelled ? Collections.emptySet() : event.getBlockEntities();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V"))
    private <E extends BlockEntity> void onRenderBlockEntity(final BlockEntityRenderDispatcher instance, final E blockEntity, final float tickDelta,
                                                             final MatrixStack matrices, final VertexConsumerProvider vertexConsumers) {
        if (this.broadcastCancellable(new PreBlockEntityRenderEvent(blockEntity))) {
            return;
        }

        instance.render(blockEntity, tickDelta, matrices, vertexConsumers);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderLayer(Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/util/math/MatrixStack;DDDLorg/joml/Matrix4f;)V"))
    private void onRenderLayer(final WorldRenderer instance, final RenderLayer renderLayer, final MatrixStack matrices,
                               final double cameraX, final double cameraY, final double cameraZ,
                               final Matrix4f positionMatrix) {
        if (this.broadcastCancellable(new RenderLayerEvent(renderLayer))) {
            return;
        }

        ((WorldRendererAccessor) instance).invokeRenderLayer(renderLayer, matrices, cameraX, cameraY, cameraZ, positionMatrix);
    }
}