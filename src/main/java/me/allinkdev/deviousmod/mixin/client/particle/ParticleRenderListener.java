package me.allinkdev.deviousmod.mixin.client.particle;

import com.github.allinkdev.deviousmod.api.managers.EventManager;
import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.render.particle.PreParticleBatchRenderEvent;
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
    @SuppressWarnings("all")
    @Redirect(method = "renderParticles", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object onRenderParticles(final Map instance, final Object o) {
        final ParticleTextureSheet textureSheet = (ParticleTextureSheet) o;
        final Queue<Particle> particleBatch = (Queue<Particle>) instance.get(textureSheet);

        if (particleBatch == null) {
            return null;
        }

        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventManager<EventBus> eventManager = deviousMod.getEventManager();
        final PreParticleBatchRenderEvent event = new PreParticleBatchRenderEvent(textureSheet, particleBatch);
        eventManager.broadcastEvent(event);

        return event.isCancelled() ? null : particleBatch;
    }
}
