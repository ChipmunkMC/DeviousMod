package me.allinkdev.deviousmod.mixin.client.entity;

import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.entity.impl.EntityVisibilityCheckEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public final class EntityVisibilityOverrider {
    @Inject(method = "isInvisibleTo", at = @At("RETURN"), cancellable = true)
    private void onIsInvisibleTo(final PlayerEntity player, final CallbackInfoReturnable<Boolean> cir) {
        final MinecraftClient client = DeviousMod.CLIENT;
        final ClientPlayerEntity clientPlayerEntity = client.player;

        if (clientPlayerEntity == null) {
            return;
        }

        if (!clientPlayerEntity.equals(player)) {
            return;
        }

        final boolean originalReturnValue = cir.getReturnValue();
        final Entity entity = (Entity) (Object) this;
        final EntityVisibilityCheckEvent event = new EntityVisibilityCheckEvent(entity, originalReturnValue);
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventBus eventBus = deviousMod.getEventBus();
        eventBus.post(event);

        final boolean newReturnValue = event.isVisible();
        cir.setReturnValue(newReturnValue);
    }
}
