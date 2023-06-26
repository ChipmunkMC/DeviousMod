package me.allinkdev.deviousmod.mixin.client.entity;

import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.self.SelfReducedDebugInfoEvent;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public final class PlayerEntityMixin {

    @Inject(method = "hasReducedDebugInfo", at = @At("RETURN"), cancellable = true)
    private void onHasReducedDebugInfo(final CallbackInfoReturnable<Boolean> cir) {
        final SelfReducedDebugInfoEvent event = new SelfReducedDebugInfoEvent(cir.getReturnValue());
        DeviousMod.getInstance().getEventBus().post(event);

        cir.setReturnValue(event.isReducedDebugInfo());
    }
}
