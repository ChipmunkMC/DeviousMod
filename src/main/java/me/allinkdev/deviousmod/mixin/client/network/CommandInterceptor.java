package me.allinkdev.deviousmod.mixin.client.network;

import me.allinkdev.deviousmod.event.self.chat.impl.SelfSendCommandEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class CommandInterceptor {
    @Shadow
    public abstract void sendChatCommand(String command);

    @Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
    public void onSendChatCommand(final String command, final CallbackInfo ci) {
        final SelfSendCommandEvent commandEvent = EventUtil.postEvent(new SelfSendCommandEvent(command));

        if (commandEvent.isCancelled()) {
            ci.cancel();
            return;
        }

        final String replacementCommand = commandEvent.getMessage();

        if (!replacementCommand.equals(command)) {
            ci.cancel();
            this.sendChatCommand(replacementCommand);
        }
    }
}
