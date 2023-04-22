package me.allinkdev.deviousmod.event.entity;

import me.allinkdev.deviousmod.event.Cancellable;
import net.minecraft.entity.Entity;

public abstract class GenericEntityEvent extends Cancellable {
    private final Entity entity;

    protected GenericEntityEvent(final Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }
}
