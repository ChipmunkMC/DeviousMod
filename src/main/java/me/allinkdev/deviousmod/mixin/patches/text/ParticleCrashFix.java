package me.allinkdev.deviousmod.mixin.patches.text;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public final class ParticleCrashFix {
    @Inject(method = "onParticle", at = @At("HEAD"), cancellable = true)
    private void onAddParticle(final ParticleS2CPacket packet, final CallbackInfo ci) {
        final int count = packet.getCount();

        if (count < 1000) {
            return;
        }

        ci.cancel();
    }
}
