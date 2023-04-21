package me.allinkdev.deviousmod.event.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.allinkdev.deviousmod.event.Cancellable;
import net.minecraft.entity.Entity;

@Getter
@RequiredArgsConstructor
public abstract class GenericEntityEvent extends Cancellable {
    private final Entity entity;
}
