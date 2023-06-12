package me.allinkdev.deviousmod.event;

import com.github.allinkdev.deviousmod.api.event.Event;
import com.github.allinkdev.deviousmod.api.managers.EventManager;
import com.google.common.eventbus.EventBus;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class DEventManager implements EventManager<EventBus> {
    private final EventBus eventBus = new EventBus();
    private final Set<Object> listeners = Collections.synchronizedSet(new LinkedHashSet<>());

    @Override
    public void registerListener(final Object listener) {
        if (this.listeners.contains(listener)) {
            return;
        }

        this.eventBus.register(listener);
        this.listeners.add(listener);
    }

    @Override
    public void unregisterListener(final Object listener) {
        if (!this.listeners.contains(listener)) {
            return;
        }

        this.eventBus.unregister(listener);
    }

    @Override
    public void broadcastEvent(final Event event) {
        this.eventBus.post(event);
    }

    @Override
    public EventBus getInternalEventSystem() {
        return this.eventBus;
    }
}
