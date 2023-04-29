package me.allinkdev.deviousmod.event.entity.impl;

import me.allinkdev.deviousmod.event.entity.GenericEntityEvent;
import net.minecraft.entity.Entity;

public final class EntityVisibilityCheckEvent extends GenericEntityEvent {
    private boolean isVisible;

    public EntityVisibilityCheckEvent(final Entity entity, final boolean isVisible) {
        super(entity);
        this.isVisible = isVisible;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    public void setVisible(final boolean visible) {
        isVisible = visible;
    }
}
