package me.allinkdev.deviousmod.mixin.accessor;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.Queue;

@Mixin(ParticleManager.class)
public interface ParticleManagerAccessor {
    @Accessor
    Queue<Particle> getNewParticles();

    @Accessor
    Map<ParticleTextureSheet, Queue<Particle>> getParticles();
}
