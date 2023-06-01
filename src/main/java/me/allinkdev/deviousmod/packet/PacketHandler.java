package me.allinkdev.deviousmod.packet;

import com.google.common.eventbus.EventBus;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import me.allinkdev.deviousmod.event.network.packet.impl.PacketC2SEvent;
import me.allinkdev.deviousmod.event.network.packet.impl.PacketS2CEvent;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.NotNull;

public final class PacketHandler extends ChannelDuplexHandler {
    private final EventBus eventBus;

    public PacketHandler(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void channelRead(@NotNull final ChannelHandlerContext ctx, @NotNull final Object msg) throws Exception {
        if (!(msg instanceof final Packet<?> packet)) {
            super.channelRead(ctx, msg);
            return;
        }

        final PacketS2CEvent event = new PacketS2CEvent(packet);
        this.eventBus.post(event);
        final Packet<?> eventPacket = event.getPacket();

        if (event.isCancelled() || eventPacket == null) {
            return;
        }

        super.channelRead(ctx, eventPacket);
    }

    @Override
    public void write(final ChannelHandlerContext ctx, final Object msg, final ChannelPromise promise) throws Exception {
        if (!(msg instanceof final Packet<?> packet)) {
            super.write(ctx, msg, promise);
            return;
        }

        final PacketC2SEvent event = new PacketC2SEvent(packet);
        this.eventBus.post(event);
        final Packet<?> eventPacket = event.getPacket();

        if (event.isCancelled() || eventPacket == null) {
            return;
        }

        super.write(ctx, eventPacket, promise);
    }
}
