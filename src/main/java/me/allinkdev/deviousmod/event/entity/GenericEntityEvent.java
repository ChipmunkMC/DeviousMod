package me.allinkdev.deviousmod.event.entity;

import com.github.allinkdev.deviousmod.api.event.Cancellable;
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
