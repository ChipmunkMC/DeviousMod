package me.allinkdev.deviousmod.mixin.client.network.packet;

import io.netty.channel.ChannelHandlerContext;
import me.allinkdev.deviousmod.event.network.packet.impl.PacketC2SEvent;
import me.allinkdev.deviousmod.event.network.packet.impl.PacketS2CEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public final class PacketSendAndReceive {
    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    //CHECKSTYLE:OFF
    public void onReceive(final ChannelHandlerContext channelHandlerContext, Packet<?> packet, final CallbackInfo ci) {
    //CHECKSTYLE:ON
	final var event = EventUtil.postEvent(new PacketS2CEvent(packet));
        final var eventPacket = event.getPacket();

        if (event.isCancelled() || eventPacket == null) ci.cancel();
        packet = eventPacket;
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"), cancellable = true)
    //CHECKSTYLE:OFF
    private void onSend(Packet<?> packet, final PacketCallbacks callbacks, final CallbackInfo ci) {
    //CHECKSTYLE:ON
	final var event = EventUtil.postEvent(new PacketC2SEvent(packet));
        final var eventPacket = event.getPacket();

        if (event.isCancelled() || eventPacket == null) ci.cancel();
        packet = eventPacket;
    }
}
