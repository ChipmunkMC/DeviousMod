package me.allinkdev.deviousmod.mixin.client.network;

import com.mojang.brigadier.suggestion.Suggestions;
import me.allinkdev.deviousmod.command.CommandCompletionManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.CommandSuggestionsS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public final class CommandCompletionStealer {
    @Inject(method = "onCommandSuggestions", at = @At("HEAD"), cancellable = true)
    private void onCommandSuggestions(final CommandSuggestionsS2CPacket packet, final CallbackInfo ci) {
        final int id = packet.getCompletionId();
        final Suggestions suggestions = packet.getSuggestions();
        final boolean handled = CommandCompletionManager.handleCompletion(id, suggestions);
        if (!handled) return;
        ci.cancel();
    }
}
