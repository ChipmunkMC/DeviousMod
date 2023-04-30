package me.allinkdev.deviousmod.event.input;

import me.allinkdev.deviousmod.event.Event;

public final class ItemUseCooldownCheckEvent extends Event {
    private int value;

    public ItemUseCooldownCheckEvent(final int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(final int value) {
        this.value = value;
    }
}
