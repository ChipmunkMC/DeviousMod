package me.allinkdev.deviousmod.event.entity.living;

import com.github.allinkdev.deviousmod.api.event.Cancellable;
import net.minecraft.entity.LivingEntity;

public abstract class GenericLivingEntityEvent extends Cancellable {
    private final LivingEntity livingEntity;

    protected GenericLivingEntityEvent(final LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
    }

    public LivingEntity getLivingEntity() {
        return this.livingEntity;
    }
}
