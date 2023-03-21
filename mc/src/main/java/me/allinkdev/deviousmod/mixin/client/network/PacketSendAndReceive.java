package me.allinkdev.deviousmod.mixin.client.network;

import com.google.common.eventbus.EventBus;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import me.allinkdev.deviousmod.event.Event;
import me.allinkdev.deviousmod.event.packet.impl.PacketC2SEvent;
import me.allinkdev.deviousmod.event.packet.impl.PacketS2CEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.OffThreadException;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.RejectedExecutionException;

@Mixin(ClientConnection.class)
public abstract class PacketSendAndReceive {
    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    private Channel channel;
    @Shadow
    private PacketListener packetListener;
    @Shadow
    private int packetsReceivedCounter;

    @SuppressWarnings("unchecked")
    private static <T extends PacketListener> void handlePacket(final Packet<T> packet, final PacketListener listener) {
        packet.apply((T) listener);
    }

    @Shadow
    public abstract void setState(NetworkState state);

    @Shadow
    public abstract void disconnect(Text disconnectReason);

    @Inject(method = "sendInternal", at = @At("HEAD"), cancellable = true)
    private void onSendInternal(final Packet<?> packet, final @Nullable PacketCallbacks callbacks, final NetworkState packetState, final NetworkState currentState, final CallbackInfo ci) {
        ci.cancel();

        customSendInternal(packet, callbacks, packetState, currentState);
    }

    private void customSendInternal(final Packet<?> originalPacket, final @Nullable PacketCallbacks originalCallbacks, final NetworkState packetState, final NetworkState currentState) {
        final PacketC2SEvent event = new PacketC2SEvent(originalPacket, originalCallbacks);
        final EventBus eventBus = Event.getEventBus();
        eventBus.post(event);

        if (event.isCancelled()) {
            return;
        }

        if (packetState != currentState) {
            this.setState(packetState);
        }

        final Packet<?> packet = event.getPacket();
        final PacketCallbacks callbacks = event.getPacketCallbacks();

        if (packet == null) {
            return;
        }

        final ChannelFuture channelFuture = this.channel.writeAndFlush(packet);

        if (event.isThrowingExceptions()) {
            channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }

        if (callbacks == null) {
            return;
        }

        channelFuture.addListener((future -> {
            if (future.isSuccess()) {
                callbacks.onSuccess();
                return;
            }

            final Packet<?> failurePacket = callbacks.getFailurePacket();

            if (failurePacket == null) {
                return;
            }

            final ChannelFuture failureChannelFuture = this.channel.writeAndFlush(failurePacket);

            if (event.isThrowingExceptions()) {
                failureChannelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            }
        }));
    }

    @Inject(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void onChannelRead0(final ChannelHandlerContext channelHandlerContext, final Packet<?> packet, final CallbackInfo ci) {
        ci.cancel();

        customRead0(packet);
    }

    public void customRead0(final Packet<?> originalPacket) {
        if (!channel.isOpen()) {
            return;
        }

        final PacketS2CEvent event = new PacketS2CEvent(originalPacket);
        final EventBus eventBus = Event.getEventBus();
        eventBus.post(event);

        if (event.isCancelled()) {
            return;
        }

        final Packet<?> packet = event.getPacket();

        try {
            handlePacket(packet, this.packetListener);
        } catch (OffThreadException exception) {
            //Ignored?
        } catch (RejectedExecutionException exception) {
            if (event.isDisconnectingOnExceptions()) {
                this.disconnect(Text.translatable("multiplayer.disconnect.server_shutdown"));
            } else {
                LOGGER.warn("Ignoring network exception!", exception);
            }
        } catch (ClassCastException exception) {
            if (event.isDisconnectingOnExceptions()) {
                LOGGER.error("Received {} that couldn't be processed!", packet.getClass().getTypeName(), exception);
                this.disconnect(Text.translatable("multiplayer.disconnect.invalid_packet"));
            } else {
                LOGGER.warn("Received {} that couldn't be processed, ignoring...", packet.getClass().getTypeName(), exception);
            }
        }

        ++this.packetsReceivedCounter;
    }
}
