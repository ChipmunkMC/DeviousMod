package me.allinkdev.deviousmod.mixin.client.input;

import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.input.ItemUseCooldownCheckEvent;
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
        final int value = itemUseCooldown;
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventBus eventBus = deviousMod.getEventBus();
        final ItemUseCooldownCheckEvent event = new ItemUseCooldownCheckEvent(value);
        eventBus.post(event);

        return event.getValue();
    }
}
