package me.allinkdev.deviousmod.event.entity.impl;

import lombok.Getter;
import me.allinkdev.deviousmod.event.entity.GenericEntityEvent;
import net.minecraft.entity.Entity;

@Getter
public class AddEntityEvent extends GenericEntityEvent {
    public AddEntityEvent(final Entity entity) {
        super(entity);
    }

    public static boolean addEntity(final Entity entity) {
        final AddEntityEvent event = new AddEntityEvent(entity);

        eventBus.post(event);

        return event.isCancelled();
    }
}
