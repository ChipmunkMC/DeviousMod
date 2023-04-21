package me.allinkdev.deviousmod.mixin.patches.entity;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AreaEffectCloudEntity.class)
public final class AreaEffectCloudParticleLimiter {
    @Inject(method = "getParticleType", at = @At("RETURN"), cancellable = true)
    private void onGetParticleType(final CallbackInfoReturnable<ParticleEffect> cir) {
        cir.setReturnValue(ParticleTypes.ENTITY_EFFECT);
    }

    @Inject(method = "getRadius", at = @At("RETURN"), cancellable = true)
    private void onGetRadius(final CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(MathHelper.clamp(cir.getReturnValue(), 0.0F, 5F));
    }

    @Inject(method = "getRadiusOnUse", at = @At("RETURN"), cancellable = true)
    private void onGetRadiusOnUse(final CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(MathHelper.clamp(cir.getReturnValue(), 0.0F, 5.0F));
    }

    @Inject(method = "getRadiusOnUse", at = @At("RETURN"), cancellable = true)
    private void onGetRadiusGrowth(final CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(MathHelper.clamp(cir.getReturnValue(), 0.0F, 5.0F));
    }
}
