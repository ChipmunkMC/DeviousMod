package me.allinkdev.deviousmod.mixin.client.network;

import com.mojang.brigadier.CommandDispatcher;
import me.allinkdev.deviousmod.event.command.CommandDispatcherSwapEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
public final class CommandDispatcherListener {
    @Shadow
    private CommandDispatcher<CommandSource> commandDispatcher;

    @Redirect(method = "onCommandTree", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;commandDispatcher:Lcom/mojang/brigadier/CommandDispatcher;", opcode = Opcodes.PUTFIELD))
    private void onPutCommandDispatcher(final ClientPlayNetworkHandler instance, final CommandDispatcher<CommandSource> value) {
        EventUtil.postEvent(new CommandDispatcherSwapEvent(this.commandDispatcher, value));
        this.commandDispatcher = value;
    }
}
