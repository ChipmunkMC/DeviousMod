package me.allinkdev.deviousmod.mixin.client.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import me.allinkdev.deviousmod.event.network.packet.impl.PrePacketS2CEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.network.DecoderHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;

@Mixin(DecoderHandler.class)
public class RawPacketReceive {

    @Inject(method = "decode", at = @At("HEAD"))
    private void onDecode(final ChannelHandlerContext ctx, final ByteBuf buf, final List<Object> objects, final CallbackInfo ci) {
        final byte[] array = Unpooled.copiedBuffer(buf.copy()).array();
        EventUtil.postEvent(new PrePacketS2CEvent(Arrays.copyOf(array, array.length)));
    }
}
