package me.allinkdev.deviousmod.mixin.client.network;

import io.netty.channel.ChannelHandlerContext;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.network.connection.ConnectionErrorEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public final class ClientConnectionMixin {
    @Inject(method = "exceptionCaught", at = @At("HEAD"), cancellable = true)
    private void onExceptionCaught(final ChannelHandlerContext context, final Throwable ex, final CallbackInfo ci) {
        if (DeviousMod.IS_EXPERIMENTAL) ex.printStackTrace();
        if (EventUtil.postCancellable(new ConnectionErrorEvent(ex))) ci.cancel();
    }
}
