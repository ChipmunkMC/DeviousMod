package me.allinkdev.deviousmod.mixin.client.input;

import me.allinkdev.deviousmod.event.input.ItemUseCooldownCheckEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.client.MinecraftClient;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MinecraftClient.class)
public final class ItemUseCooldownReplacer {
    @Shadow
    private int itemUseCooldown;

    @Redirect(method = "handleInputEvents", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;itemUseCooldown:I", opcode = Opcodes.GETFIELD))
    private int onItemUseCooldown(final MinecraftClient instance) {
        return EventUtil.postEvent(new ItemUseCooldownCheckEvent(itemUseCooldown)).getValue();
    }
}
