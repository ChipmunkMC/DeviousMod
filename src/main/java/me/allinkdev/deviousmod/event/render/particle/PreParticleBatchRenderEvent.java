package me.allinkdev.deviousmod.event.render.particle;

import com.github.allinkdev.deviousmod.api.event.Cancellable;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleTextureSheet;

import java.util.Queue;

public class PreParticleBatchRenderEvent extends Cancellable {
    private final ParticleTextureSheet textureSheet;
    private final Queue<Particle> particleBatch;

    public PreParticleBatchRenderEvent(final ParticleTextureSheet textureSheet, final Queue<Particle> particleBatch) {
        this.textureSheet = textureSheet;
        this.particleBatch = particleBatch;
    }

    public ParticleTextureSheet getTextureSheet() {
        return this.textureSheet;
    }

    public Queue<Particle> getParticleBatch() {
        return this.particleBatch;
    }
}
