package me.allinkdev.deviousmod.mixin.client.render.world;

import com.mojang.blaze3d.systems.RenderSystem;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.render.block.PreBlockEntityRenderEvent;
import me.allinkdev.deviousmod.event.render.block.PreUncullableBlockEntityRenderEvent;
import me.allinkdev.deviousmod.event.render.entity.EntityRenderPipelineEvent;
import me.allinkdev.deviousmod.event.render.entity.RenderLayerEvent;
import me.allinkdev.deviousmod.event.render.world.PrePlanetaryBodyRenderEvent;
import me.allinkdev.deviousmod.event.render.world.PreSkyRenderEvent;
import me.allinkdev.deviousmod.event.render.world.PreStarRenderEvent;
import me.allinkdev.deviousmod.event.render.world.PreWeatherRenderEvent;
import me.allinkdev.deviousmod.mixin.accessor.ResourceTextureAccessor;
import me.allinkdev.deviousmod.mixin.accessor.WorldRendererAccessor;
import me.allinkdev.deviousmod.util.EventUtil;
import me.allinkdev.deviousmod.util.IterUtil;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Mixin(WorldRenderer.class)
public abstract class WorldRenderListener {
    @Shadow
    @Final
    private static Identifier SUN;
    @Shadow
    @Final
    private static Identifier MOON_PHASES;
    @Shadow
    @Final
    private Set<BlockEntity> noCullingBlockEntities;
    @Shadow
    private @Nullable VertexBuffer starsBuffer;

    @Shadow
    protected abstract void renderStars();

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getEntities()Ljava/lang/Iterable;"))
    private Iterable<Entity> onRender(final ClientWorld instance) {
        final Iterable<Entity> entityIterable = instance.getEntities();
        final List<Entity> entities = IterUtil.toList(entityIterable);

        return EventUtil.postCancellable(new EntityRenderPipelineEvent(entities)) ? Collections.emptyList() : entities;
    }

    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/WorldRenderer;noCullingBlockEntities:Ljava/util/Set;", opcode = Opcodes.ACC_SYNCHRONIZED))
    private Set<BlockEntity> onGetNoCullingBlockEntities(final WorldRenderer instance) {
        final PreUncullableBlockEntityRenderEvent event = new PreUncullableBlockEntityRenderEvent(this.noCullingBlockEntities);
        return EventUtil.postCancellable(event) ? Collections.emptySet() : event.getBlockEntities();
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V"))
    private <E extends BlockEntity> void onRenderBlockEntity(final BlockEntityRenderDispatcher instance, final E blockEntity, final float tickDelta,
                                                             final MatrixStack matrices, final VertexConsumerProvider vertexConsumers) {
        if (EventUtil.postCancellable(new PreBlockEntityRenderEvent(blockEntity))) {
            return;
        }

        instance.render(blockEntity, tickDelta, matrices, vertexConsumers);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderLayer(Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/util/math/MatrixStack;DDDLorg/joml/Matrix4f;)V"))
    private void onRenderLayer(final WorldRenderer instance, final RenderLayer renderLayer, final MatrixStack matrices,
                               final double cameraX, final double cameraY, final double cameraZ,
                               final Matrix4f positionMatrix) {
        if (EventUtil.postCancellable(new RenderLayerEvent(renderLayer))) {
            return;
        }

        ((WorldRendererAccessor) instance).invokeRenderLayer(renderLayer, matrices, cameraX, cameraY, cameraZ, positionMatrix);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderWeather(Lnet/minecraft/client/render/LightmapTextureManager;FDDD)V"))
    private void onRenderWeather(final WorldRenderer instance, final LightmapTextureManager manager, final float tickDelta,
                                 final double cameraX, final double cameraY, final double cameraZ) {
        if (EventUtil.postCancellable(new PreWeatherRenderEvent())) {
            return;
        }

        ((WorldRendererAccessor) instance).invokeRenderWeather(manager, tickDelta, cameraX, cameraY, cameraZ);
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V"))
    private void onRenderSky(final WorldRenderer instance, final MatrixStack matrices, final Matrix4f projectionMatrix, final float tickDelta,
                             final Camera camera, final boolean bl, final Runnable runnable) {
        if (EventUtil.postCancellable(new PreSkyRenderEvent())) {
            return;
        }

        instance.renderSky(matrices, projectionMatrix, tickDelta, camera, bl, runnable);
    }

    @Redirect(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/BufferRenderer;drawWithGlobalProgram(Lnet/minecraft/client/render/BufferBuilder$BuiltBuffer;)V"))
    private void onDrawWithGlobalProgram(final BufferBuilder.BuiltBuffer buffer) {
        final int currentTexture = RenderSystem.getShaderTexture(0);
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final Optional<AbstractTexture> abstractTextureOptional = deviousMod.getRenderManager().searchForTexture(currentTexture);

        if (abstractTextureOptional.isEmpty()) {
            //DeviousMod.LOGGER.info("Could not find abstract texture for {}", currentTexture);
            BufferRenderer.drawWithGlobalProgram(buffer);
            return;
        }

        final AbstractTexture abstractTexture = abstractTextureOptional.get();

        if (!(abstractTexture instanceof final ResourceTexture resourceTexture)) {
            //DeviousMod.LOGGER.warn("Abstract texture for {} was not an instance of resource texture, passing through. Class is {}", currentTexture, abstractTexture.getClass().getName());
            BufferRenderer.drawWithGlobalProgram(buffer);
            return;
        }

        final Identifier identifier = ((ResourceTextureAccessor) resourceTexture).getLocation();

        if (!identifier.equals(SUN) && !identifier.equals(MOON_PHASES)) {
            BufferRenderer.drawWithGlobalProgram(buffer);
            return;
        }

        if (EventUtil.postCancellable(new PrePlanetaryBodyRenderEvent())) {
            buffer.release();
            return;
        }

        BufferRenderer.drawWithGlobalProgram(buffer);
    }

    // TODO: De-duplicate event broadcasts
    @Redirect(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/VertexBuffer;bind()V"))
    private void onRenderSky$starBuffers$bind(final VertexBuffer instance) {
        if (this.starsBuffer == null) {
            instance.bind();
            return;
        }

        if (!instance.equals(this.starsBuffer)) {
            instance.bind();
            return;
        }

        if (EventUtil.postCancellable(new PreStarRenderEvent())) {
            return;
        }

        instance.bind();
    }


    @Redirect(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/VertexBuffer;draw(Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;Lnet/minecraft/client/gl/ShaderProgram;)V"))
    private void onRenderSky$starBuffers$draw(final VertexBuffer instance, final Matrix4f viewMatrix, final Matrix4f projectionMatrix, final ShaderProgram program) {
        if (this.starsBuffer == null) {
            instance.draw(viewMatrix, projectionMatrix, program);
            return;
        }

        if (!instance.equals(this.starsBuffer)) {
            instance.draw(viewMatrix, projectionMatrix, program);
            return;
        }

        if (EventUtil.postCancellable(new PreStarRenderEvent())) {
            return;
        }

        instance.draw(viewMatrix, projectionMatrix, program);
    }
}