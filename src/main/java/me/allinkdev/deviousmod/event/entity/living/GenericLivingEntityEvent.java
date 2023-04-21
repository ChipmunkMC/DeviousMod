package me.allinkdev.deviousmod.event.entity.living;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import me.allinkdev.deviousmod.event.Cancellable;
import net.minecraft.entity.LivingEntity;

@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor
public abstract class GenericLivingEntityEvent extends Cancellable {
    private final LivingEntity livingEntity;
}
