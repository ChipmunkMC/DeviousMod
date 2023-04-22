package me.allinkdev.deviousmod.event.entity.impl;

import me.allinkdev.deviousmod.event.entity.GenericEntityEvent;
import net.minecraft.entity.Entity;

public final class EntityAddEvent extends GenericEntityEvent {
    public EntityAddEvent(final Entity entity) {
        super(entity);
    }
}
