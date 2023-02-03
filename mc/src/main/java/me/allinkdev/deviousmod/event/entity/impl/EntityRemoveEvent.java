package me.allinkdev.deviousmod.event.entity.impl;

import me.allinkdev.deviousmod.event.entity.GenericEntityEvent;
import net.minecraft.entity.Entity;

public class EntityRemoveEvent extends GenericEntityEvent {
    public EntityRemoveEvent(Entity entity) {
        super(entity);
    }

    public static boolean removeEntity(final Entity entity) {
        final EntityRemoveEvent event = new EntityRemoveEvent(entity);

        eventBus.post(event);

        return event.isCancelled();
    }
}
