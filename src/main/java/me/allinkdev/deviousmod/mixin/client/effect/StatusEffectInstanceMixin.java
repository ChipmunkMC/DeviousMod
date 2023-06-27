package me.allinkdev.deviousmod.mixin.client.effect;

import me.allinkdev.deviousmod.event.effect.ShowStatusEffectIconEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectInstance.class)
public final class StatusEffectInstanceMixin {

    @Inject(method = "shouldShowIcon", at = @At("RETURN"), cancellable = true)
    public void onShowIconIf(final CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(EventUtil.postEvent(new ShowStatusEffectIconEvent((StatusEffectInstance) (Object) this, cir.getReturnValue())).isShowIcon());
    }
}
