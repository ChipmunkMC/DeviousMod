package me.allinkdev.deviousmod.mixin.patches.particle;

import me.allinkdev.deviousmod.mixin.accessor.ParticleManagerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public final class ParticleCrashFix {
    @Shadow
    @Final
    private MinecraftClient client;

    private boolean withinBoundsFloat(final float value) {
        return !Float.isNaN(value) && Float.isFinite(value) && value < (Float.MIN_VALUE * 2);
    }

    @Inject(method = "onParticle", at = @At("HEAD"), cancellable = true)
    private void onAddParticle(final ParticleS2CPacket packet, final CallbackInfo ci) {
        final int count = packet.getCount();

        if (count <= 0) {
            ci.cancel();
            return;
        }


        if (!withinBoundsFloat(packet.getSpeed())) {
            ci.cancel();
            return;
        }

        final long packetParticleCount = packet.getCount();

        if (packetParticleCount > 99) {
            ci.cancel();
            return;
        }

        final ParticleManagerAccessor particleManagerAccessor = (ParticleManagerAccessor) this.client.particleManager;
        final long estimatedCount = (long) particleManagerAccessor.getNewParticles().size() + particleManagerAccessor.getParticles().entrySet().size() + packetParticleCount;

        if (estimatedCount < 500) {
            return;
        }

        ci.cancel();
    }
}
