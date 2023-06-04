package me.allinkdev.deviousmod.event.screen.impl;

import com.github.allinkdev.deviousmod.api.event.Event;
import net.minecraft.client.gui.screen.Screen;

public final class SetScreenEvent implements Event {
    private Screen target;

    public SetScreenEvent(final Screen target) {
        this.target = target;
    }

    public Screen getTarget() {
        return this.target;
    }

    public void setTarget(final Screen newTarget) {
        this.target = newTarget;
    }
}
