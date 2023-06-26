package me.allinkdev.deviousmod.mixin.client.effect;

import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.effect.ShowStatusEffectIconEvent;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectInstance.class)
public final class StatusEffectInstanceMixin {

    @Inject(method = "shouldShowIcon", at = @At("HEAD"), cancellable = true)
    public void onShowIconIf(final CallbackInfoReturnable<Boolean> cir) {
        final ShowStatusEffectIconEvent event = new ShowStatusEffectIconEvent((StatusEffectInstance) (Object) this, cir.getReturnValue());
        DeviousMod.getInstance().getEventBus().post(event);

        cir.setReturnValue(event.isShowIcon());
    }
}
