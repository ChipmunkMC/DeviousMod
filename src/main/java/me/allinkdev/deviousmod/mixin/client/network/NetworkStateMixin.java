package me.allinkdev.deviousmod.mixin.client.network;

import com.google.common.eventbus.EventBus;
import io.netty.channel.ChannelHandlerContext;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.network.connection.ConnectionEndEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionStartEvent;
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

        if (packetListener instanceof ClientQueryPacketListener) {
            return;
        }

        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventBus eventBus = deviousMod.getEventBus();
        final ConnectionEndEvent event = new ConnectionEndEvent();
        eventBus.post(event);
    }

    @Inject(method = "channelActive", at = @At("HEAD"))
    public void onChannelActive(final ChannelHandlerContext context, final CallbackInfo ci) {
        final PacketListener packetListener = this.getPacketListener();

        if (packetListener instanceof ClientQueryPacketListener) {
            return;
        }

        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventBus eventBus = deviousMod.getEventBus();
        final ConnectionStartEvent event = new ConnectionStartEvent();
        eventBus.post(event);
    }
}
