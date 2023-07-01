package me.allinkdev.deviousmod.mixin.client.network;

import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.self.chat.impl.SelfSendCommandEvent;
import me.allinkdev.deviousmod.queue.CommandQueueManager;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class CommandInterceptor {
    @Shadow
    public abstract void sendChatCommand(String command);

    @Shadow
    public abstract boolean sendCommand(String command);

    private void handle(final String command, final boolean properlySigned, final CallbackInfo ci) {
        final CommandQueueManager commandQueueManager = DeviousMod.getInstance().getCommandQueueManager();
        final SelfSendCommandEvent commandEvent = EventUtil.postEvent(new SelfSendCommandEvent(command, commandQueueManager.isQueuedAndIfSoRemove(command)));

        if (commandEvent.isCancelled()) {
            ci.cancel();
            return;
        }

        final String replacementCommand = commandEvent.getMessage();

        if (!replacementCommand.equals(command)) {
            ci.cancel();

            if (properlySigned) {
                this.sendCommand(replacementCommand);
            } else {
                this.sendChatCommand(replacementCommand);
            }
        }
    }

    @Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
    private void onSendCommand(final String command, final CallbackInfoReturnable<Boolean> cir) {
        this.handle(command, false, cir);
    }

    @Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
    public void onSendChatCommand(final String command, final CallbackInfo ci) {
        this.handle(command, true, ci);
    }
}
