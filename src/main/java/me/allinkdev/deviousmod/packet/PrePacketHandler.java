package me.allinkdev.deviousmod.packet;

import com.github.allinkdev.deviousmod.api.managers.EventManager;
import com.google.common.eventbus.EventBus;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.network.packet.impl.PrePacketC2SEvent;
import me.allinkdev.deviousmod.event.network.packet.impl.PrePacketS2CEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PrePacketHandler extends ChannelDuplexHandler {
    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
        if (msg instanceof final ByteBuf byteBuf) {
            final byte[] array = Unpooled.copiedBuffer(byteBuf.copy()).array();

            final DeviousMod deviousMod = DeviousMod.getInstance();
            final EventManager<EventBus> eventManager = deviousMod.getEventManager();
            eventManager.broadcastEvent(new PrePacketC2SEvent(Arrays.copyOf(array, array.length)));
        } else {
            DeviousMod.LOGGER.warn("Received non-ByteBuf object ({}) in pre packet C2S handler.", msg.getClass().getSimpleName());
        }

        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(final @NotNull ChannelHandlerContext ctx, final @NotNull Object msg) throws Exception {
        if (msg instanceof final ByteBuf byteBuf) {
            final byte[] array = Unpooled.copiedBuffer(byteBuf.copy()).array();

            final DeviousMod deviousMod = DeviousMod.getInstance();
            final EventManager<EventBus> eventManager = deviousMod.getEventManager();
            eventManager.broadcastEvent(new PrePacketS2CEvent(Arrays.copyOf(array, array.length)));
        } else {
            DeviousMod.LOGGER.warn("Received non-ByteBuf object ({}) in pre S2C packet handler.", msg.getClass().getSimpleName());
        }

        super.channelRead(ctx, msg);
    }
}
