package me.allinkdev.deviousmod.mixin.client.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import me.allinkdev.deviousmod.event.network.packet.impl.PrePacketC2SEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(PacketEncoder.class)
public class RawPacketSend {

    @Inject(method = "encode(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;Lio/netty/buffer/ByteBuf;)V", at = @At("HEAD"))
    private void onDecode(final ChannelHandlerContext channelHandlerContext, final Packet<?> packet, final ByteBuf byteBuf, final CallbackInfo ci) {
        final byte[] array = Unpooled.copiedBuffer(byteBuf.copy()).array();
        EventUtil.postEvent(new PrePacketC2SEvent(Arrays.copyOf(array, array.length)));
    }
}
