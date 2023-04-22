package me.allinkdev.deviousmod.event;

public class Cancellable extends Event {
    private boolean cancelled = false;

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(final boolean newValue) {
        this.cancelled = newValue;
    }
}
