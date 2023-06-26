package me.allinkdev.deviousmod.event.render.entity;

import com.github.allinkdev.deviousmod.api.event.Cancellable;
import net.minecraft.entity.Entity;

import java.util.List;

public class EntityRenderPipelineEvent extends Cancellable {
    private final List<Entity> entityList;

    public EntityRenderPipelineEvent(final List<Entity> entityList) {
        this.entityList = entityList;
    }

    public List<Entity> getEntityList() {
        synchronized (this.entityList) {
            return entityList;
        }
    }
}
