package me.allinkdev.deviousmod.event.input;

import com.github.allinkdev.deviousmod.api.event.Event;

public final class ItemUseCooldownCheckEvent implements Event {
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
