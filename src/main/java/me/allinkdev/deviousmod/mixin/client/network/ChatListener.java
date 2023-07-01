package me.allinkdev.deviousmod.mixin.client.network;

import me.allinkdev.deviousmod.event.chat.ChatEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public final class ChatListener {
    @Inject(method = "onGameMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/message/MessageHandler;onGameMessage(Lnet/minecraft/text/Text;Z)V"))
    private void messageHandler$onGameMessage(final GameMessageS2CPacket packet, final CallbackInfo ci) {
        EventUtil.postEvent(new ChatEvent(packet.content(), Util.NIL_UUID, ChatEvent.Type.SYSTEM));
    }

    @Inject(method = "onProfilelessChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/message/MessageHandler;onProfilelessMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageType$Parameters;)V"))
    private void messageHandler$onProfilelessChatMessage(final ProfilelessChatMessageS2CPacket packet, final CallbackInfo ci) {
        EventUtil.postEvent(new ChatEvent(packet.message(), Util.NIL_UUID, ChatEvent.Type.DISGUISED));
    }

    @Inject(method = "onChatMessage", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/message/MessageHandler;onChatMessage(Lnet/minecraft/network/message/SignedMessage;Lcom/mojang/authlib/GameProfile;Lnet/minecraft/network/message/MessageType$Parameters;)V"))
    private void messageHandler$onChatMessage(final ChatMessageS2CPacket packet, final CallbackInfo ci) {
        EventUtil.postEvent(new ChatEvent(packet.unsignedContent(), packet.sender(), ChatEvent.Type.PLAYER));
    }
}
