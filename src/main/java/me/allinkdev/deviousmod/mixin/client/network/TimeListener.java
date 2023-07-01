package me.allinkdev.deviousmod.mixin.client.network;

import me.allinkdev.deviousmod.event.time.second.ServerSecondEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public final class TimeListener {
    @Inject(method = "onWorldTimeUpdate", at = @At("HEAD"))
    private void onWorldTimeUpdate(final WorldTimeUpdateS2CPacket packet, final CallbackInfo ci) {
        EventUtil.postEvent(new ServerSecondEvent());
    }
}
