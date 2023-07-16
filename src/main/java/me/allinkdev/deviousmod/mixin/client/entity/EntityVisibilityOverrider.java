package me.allinkdev.deviousmod.mixin.client.entity;

import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.entity.impl.EntityVisibilityCheckEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import me.allinkdev.deviousmod.util.ObjectUtil;
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

        if (!ObjectUtil.nullSafeEquals(clientPlayerEntity, player)) return;
        final boolean originalReturnValue = cir.getReturnValue();
        final Entity entity = (Entity) (Object) this;

        cir.setReturnValue(EventUtil.postEvent(new EntityVisibilityCheckEvent(entity, originalReturnValue)).isVisible());
    }
}
