package me.allinkdev.deviousmod.mixin.client.network;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public final class MapDisplayNamePatch {
    @Inject(method = "onMapUpdate", at = @At("HEAD"))
    private void onMapUpdate(final MapUpdateS2CPacket packet, final CallbackInfo ci) {
        //
    }
}
