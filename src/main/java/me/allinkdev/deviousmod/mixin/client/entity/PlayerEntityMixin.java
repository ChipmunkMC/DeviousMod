package me.allinkdev.deviousmod.mixin.client.entity;

import me.allinkdev.deviousmod.event.self.SelfReducedDebugInfoEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public final class PlayerEntityMixin {

    @Inject(method = "hasReducedDebugInfo", at = @At("RETURN"), cancellable = true)
    private void onHasReducedDebugInfo(final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(EventUtil.postEvent(new SelfReducedDebugInfoEvent(cir.getReturnValue())).isReducedDebugInfo());
    }
}
