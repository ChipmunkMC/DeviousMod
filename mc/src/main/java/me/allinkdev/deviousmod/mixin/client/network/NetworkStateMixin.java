package me.allinkdev.deviousmod.mixin.client.network;

import com.google.common.eventbus.EventBus;
import io.netty.channel.ChannelHandlerContext;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.network.connection.ConnectionEndEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionStartEvent;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public final class NetworkStateMixin {
    @Inject(method = "channelInactive", at = @At("HEAD"))
    public void onChannelInactive(final ChannelHandlerContext context, final CallbackInfo ci) {
        final DeviousMod deviousMod = DeviousMod.getINSTANCE();
        final EventBus eventBus = deviousMod.getEventBus();
        final ConnectionEndEvent event = new ConnectionEndEvent();
        eventBus.post(event);
    }

    @Inject(method = "channelActive", at = @At("HEAD"))
    public void onChannelActive(final ChannelHandlerContext context, final CallbackInfo ci) {
        final DeviousMod deviousMod = DeviousMod.getINSTANCE();
        final EventBus eventBus = deviousMod.getEventBus();
        final ConnectionStartEvent event = new ConnectionStartEvent();
        eventBus.post(event);
    }
}
