package me.allinkdev.deviousmod.mixin.patches.network;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public final class LongCommandCompletionsKickFix {
    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"), cancellable = true)
    public void onSend(final Packet<?> packet, final PacketCallbacks callbacks, final CallbackInfo ci) {
        if (!(packet instanceof final RequestCommandCompletionsC2SPacket completionsPacket)) {
            return;
        }

        if (completionsPacket.getPartialCommand().length() <= 2048) {
            return;
        }

        ci.cancel();
    }
}
