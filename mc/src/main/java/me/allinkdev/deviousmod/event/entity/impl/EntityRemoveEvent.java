package me.allinkdev.deviousmod.event.entity.impl;

import me.allinkdev.deviousmod.event.entity.GenericEntityEvent;
import net.minecraft.entity.Entity;

public final class EntityRemoveEvent extends GenericEntityEvent {
    public EntityRemoveEvent(final Entity entity) {
        super(entity);
    }
}
