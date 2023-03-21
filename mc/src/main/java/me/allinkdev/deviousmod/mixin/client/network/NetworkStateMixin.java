package me.allinkdev.deviousmod.mixin.client.network;

import me.allinkdev.deviousmod.util.TimeUtil;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// TODO: Replace with proper event
@Mixin(ClientConnection.class)
public final class NetworkStateMixin {
    @Inject(method = "disconnect", at = @At("HEAD"))
    public void onDisconnect(final Text disconnectReason, final CallbackInfo ci) {
        TimeUtil.invalidateCommandDelay();
    }
}
