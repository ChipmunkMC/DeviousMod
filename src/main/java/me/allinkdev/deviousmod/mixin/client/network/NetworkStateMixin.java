package me.allinkdev.deviousmod.mixin.client.network;

import io.netty.channel.ChannelHandlerContext;
import me.allinkdev.deviousmod.event.network.connection.ConnectionEndEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionStartEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientQueryPacketListener;
import net.minecraft.network.listener.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public abstract class NetworkStateMixin {
    @Shadow
    public abstract PacketListener getPacketListener();

    @Inject(method = "channelInactive", at = @At("HEAD"))
    public void onChannelInactive(final ChannelHandlerContext context, final CallbackInfo ci) {
        final PacketListener packetListener = this.getPacketListener();
        if (packetListener instanceof ClientQueryPacketListener) return;
        EventUtil.postEvent(new ConnectionEndEvent());
    }

    @Inject(method = "channelActive", at = @At("HEAD"))
    public void onChannelActive(final ChannelHandlerContext context, final CallbackInfo ci) {
        final PacketListener packetListener = this.getPacketListener();
        if (packetListener instanceof ClientQueryPacketListener) return;
        EventUtil.postEvent(new ConnectionStartEvent());
    }
}
