package me.allinkdev.deviousmod.event.entity.impl;

import com.github.allinkdev.deviousmod.api.event.Event;
import net.minecraft.entity.Entity;

import java.util.Collection;
import java.util.List;

public final class PreEntitiesRenderEvent implements Event {
    private final List<Entity> entities;

    public PreEntitiesRenderEvent(final List<Entity> entities) {
        this.entities = entities;
    }

    public void addEntity(final Entity entity) {
        this.entities.add(entity);
    }

    public void addEntities(final Collection<? extends Entity> entities) {
        this.entities.addAll(entities);
    }
}

