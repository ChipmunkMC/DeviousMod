package com.github.allinkdev.deviousmod.api.managers;

import com.github.allinkdev.deviousmod.api.DeviousModSilhouette;
import com.github.allinkdev.deviousmod.api.event.Event;

/**
 * A system that handles broadcasting events, adding &amp; removing listeners, tied to a single {@link DeviousModSilhouette}
 */
public interface EventManager<T> {
    /**
     * Register a listener for use
     *
     * @param listener The listener to register
     */
    void registerListener(final Object listener);

    /**
     * Unregister a listener
     *
     * @param listener The listener to unregister
     */
    void unregisterListener(final Object listener);

    /**
     * Broadcasts an event
     *
     * @param event The {@link Event}
     */
    void broadcastEvent(final Event event);

    /**
     * Gets the internal event system that the {@link EventManager<T>} handles
     *
     * @return the internal event system
     */
    T getInternalEventSystem();
}
