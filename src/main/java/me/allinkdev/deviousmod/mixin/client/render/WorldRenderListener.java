package me.allinkdev.deviousmod.mixin.client.render;

import com.github.allinkdev.deviousmod.api.managers.EventManager;
import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.render.block.PreBlockEntityRenderEvent;
import me.allinkdev.deviousmod.event.render.block.PreUncullableBlockEntityRenderEvent;
import me.allinkdev.deviousmod.event.render.entity.EntityRenderPipelineEvent;
import me.allinkdev.deviousmod.util.IterUtil;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
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

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getEntities()Ljava/lang/Iterable;"))
    private Iterable<Entity> onRender(final ClientWorld instance) {
        final Iterable<Entity> entityIterable = instance.getEntities();
        final List<Entity> entities = IterUtil.toList(entityIterable);
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventManager<EventBus> eventManager = deviousMod.getEventManager();
        final EntityRenderPipelineEvent event = new EntityRenderPipelineEvent(entities);
        eventManager.broadcastEvent(event);

        return event.isCancelled() ? Collections.emptyList() : entities;
    }

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/WorldRenderer;noCullingBlockEntities:Ljava/util/Set;", opcode = Opcodes.ACC_SYNCHRONIZED))
    private Set<BlockEntity> onGetNoCullingBlockEntities(final WorldRenderer instance) {
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventManager<EventBus> eventManager = deviousMod.getEventManager();
        final PreUncullableBlockEntityRenderEvent event = new PreUncullableBlockEntityRenderEvent(this.noCullingBlockEntities);
        eventManager.broadcastEvent(event);

        return event.isCancelled() ? Collections.emptySet() : event.getBlockEntities();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V"))
    private <E extends BlockEntity> void onRenderBlockEntity(final BlockEntityRenderDispatcher instance, final E blockEntity, final float tickDelta,
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