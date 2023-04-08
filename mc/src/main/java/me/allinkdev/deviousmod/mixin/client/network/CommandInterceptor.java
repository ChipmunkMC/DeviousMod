package me.allinkdev.deviousmod.mixin.client.network;

import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.self.chat.impl.SelfSendCommandEvent;
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
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventBus eventBus = deviousMod.getEventBus();
        final SelfSendCommandEvent commandEvent = new SelfSendCommandEvent(command);
        eventBus.post(commandEvent);

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
