package me.allinkdev.deviousmod.mixin.client.particle;

import me.allinkdev.deviousmod.event.render.particle.PreParticleBatchRenderEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;
import java.util.Queue;

@Mixin(ParticleManager.class)
public final class ParticleRenderListener {
    @Redirect(method = "renderParticles", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object onRenderParticles(final Map<ParticleTextureSheet, Queue<Particle>> instance, final Object o) {
        if (!(o instanceof final ParticleTextureSheet textureSheet)) return null;
        final Queue<Particle> particleBatch = instance.get(textureSheet);
        if (particleBatch == null) return null;
        return EventUtil.postCancellable(new PreParticleBatchRenderEvent(textureSheet, particleBatch)) ? null : particleBatch;
    }
}
