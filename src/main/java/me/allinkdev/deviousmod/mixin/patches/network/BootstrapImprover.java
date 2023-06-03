package me.allinkdev.deviousmod.mixin.patches.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import me.allinkdev.deviousmod.DeviousMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net/minecraft/network/ClientConnection$1", remap = false /* This class inherits from Netty, so there is no need to remap it */)
public final class BootstrapImprover {
    private <T> void setConfigOptionIgnoringExceptions(final Channel channel, final ChannelOption<T> option, final T value) {
        try {
            channel.config().setOption(option, value);
        } catch (Throwable ex) {
            DeviousMod.LOGGER.warn("Bootstrap option set failure", ex);
        }

    }

    @Inject(method = "initChannel", at = @At("HEAD"))
    private void onInitChannel(final Channel channel, final CallbackInfo ci) {
        setConfigOptionIgnoringExceptions(channel, ChannelOption.IP_TOS, 0x28); // This tells your routing device to give the packets you send "critical" priority. https://netnix.org/qos.html
        setConfigOptionIgnoringExceptions(channel, ChannelOption.SO_KEEPALIVE, false); // Minecraft already has a keep-alive system. This is unnecessary.
    }
}
