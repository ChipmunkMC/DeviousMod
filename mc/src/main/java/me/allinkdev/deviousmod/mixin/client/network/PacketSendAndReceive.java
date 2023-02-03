package me.allinkdev.deviousmod.mixin.client.network;

import com.google.common.eventbus.EventBus;
import io.netty.channel.ChannelHandlerContext;
import me.allinkdev.deviousmod.event.Event;
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
        final PacketC2SEvent event = new PacketC2SEvent(packet);
        final EventBus eventBus = Event.getEventBus();
        eventBus.post(event);

        if (!event.isCancelled()) {
            return;
        }

        ci.cancel();
    }

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void onChannelRead0(final ChannelHandlerContext channelHandlerContext, final Packet<?> packet, final CallbackInfo ci) {
        final PacketS2CEvent event = new PacketS2CEvent(packet);
        final EventBus eventBus = Event.getEventBus();
        eventBus.post(event);

        if (!event.isCancelled()) {
            return;
        }

        ci.cancel();
    }
}
