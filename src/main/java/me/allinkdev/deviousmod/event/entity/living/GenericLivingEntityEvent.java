package me.allinkdev.deviousmod.event.entity.living;

import me.allinkdev.deviousmod.event.Cancellable;
import net.minecraft.entity.LivingEntity;

public abstract class GenericLivingEntityEvent extends Cancellable {
    private final LivingEntity livingEntity;

    protected GenericLivingEntityEvent(final LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }
}
