package com.github.allinkdev.deviousmod.api.event;

/**
 * A class that represents a cancellable event.
 */
public abstract class Cancellable implements Event {
    private boolean cancelled;

    /**
     * @return if the event has been marked as cancelled
     */
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Changes if an event is cancelled
     *
     * @param newValue the new cancelled value
     */
    public void setCancelled(final boolean newValue) {
        this.cancelled = true;
    }

    /**
     * Cancel an event
     */
    public void cancel() {
        this.cancelled = true;
    }

    /**
     * Uncancel an event
     */
    public void uncancel() {
        this.cancelled = false;
    }
}
