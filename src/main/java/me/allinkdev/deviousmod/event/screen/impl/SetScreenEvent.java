package me.allinkdev.deviousmod.event.screen.impl;

import me.allinkdev.deviousmod.event.Event;
import net.minecraft.client.gui.screen.Screen;

public final class SetScreenEvent extends Event {
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
