package me.allinkdev.deviousmod.mixin.client.network;

import io.netty.channel.ChannelHandlerContext;
import me.allinkdev.deviousmod.event.packet.impl.PacketC2SEvent;
import me.allinkdev.deviousmod.event.packet.impl.PacketS2CEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketCallbacks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class PacketSendAndReceive {
    @Inject(method = "sendInternal", at = @At("HEAD"), cancellable = true)
    public void onSendInternal(final Packet<?> packet, final @Nullable PacketCallbacks callbacks, final NetworkState packetState, final NetworkState currentState, final CallbackInfo ci) {
        final boolean cancelled = PacketC2SEvent.packetC2S(packet);

        if (!cancelled) {
            return;
        }

        ci.cancel();
    }

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void onChannelRead0(final ChannelHandlerContext channelHandlerContext, final Packet<?> packet, final CallbackInfo ci) {
        final boolean cancelled = PacketS2CEvent.packetS2C(packet);

        if (!cancelled) {
            return;
        }

        ci.cancel();
    }
}
