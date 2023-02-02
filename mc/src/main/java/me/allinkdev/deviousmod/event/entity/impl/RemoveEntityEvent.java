package me.allinkdev.deviousmod.event.entity.impl;

import me.allinkdev.deviousmod.event.entity.GenericEntityEvent;
import net.minecraft.entity.Entity;

public class RemoveEntityEvent extends GenericEntityEvent {
    public RemoveEntityEvent(Entity entity) {
        super(entity);
    }

    public static boolean removeEntity(final Entity entity) {
        final RemoveEntityEvent event = new RemoveEntityEvent(entity);

        eventBus.post(event);

        return event.isCancelled();
    }
}
