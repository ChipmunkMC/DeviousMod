package me.allinkdev.deviousmod.event.entity.impl;

import lombok.Getter;
import me.allinkdev.deviousmod.event.entity.GenericEntityEvent;
import net.minecraft.entity.Entity;

@Getter
public class EntityAddEvent extends GenericEntityEvent {
    public EntityAddEvent(final Entity entity) {
        super(entity);
    }

    public static boolean addEntity(final Entity entity) {
        final EntityAddEvent event = new EntityAddEvent(entity);

        eventBus.post(event);

        return event.isCancelled();
    }
}
