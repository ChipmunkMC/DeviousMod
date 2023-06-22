package me.allinkdev.deviousmod.mixin.client.network;

import com.google.common.eventbus.EventBus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.packet.PacketHandler;
import me.allinkdev.deviousmod.packet.PrePacketHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public final class PacketSendAndReceive {
    @Inject(method = "addHandlers", at = @At(value = "TAIL"))
    private static void onAddHandlers(final ChannelPipeline pipeline, final NetworkSide side, final CallbackInfo ci) {
        if (!side.equals(NetworkSide.CLIENTBOUND)) {
            return;
        }

        pipeline.addFirst(new PrePacketHandler());
    }

    @Inject(method = "channelActive", at = @At(value = "TAIL"))
    private void onChannelActive(final ChannelHandlerContext context, final CallbackInfo ci) {
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventBus eventBus = deviousMod.getEventBus();
        final PacketHandler packetHandler = new PacketHandler(eventBus);
        final ChannelPipeline channelPipeline = context.pipeline();

        channelPipeline.addBefore("packet_handler", null, packetHandler);
    }
}
