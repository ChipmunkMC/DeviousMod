package me.allinkdev.deviousmod.event;

import com.github.allinkdev.deviousmod.api.event.Event;
import com.github.allinkdev.deviousmod.api.managers.EventManager;
import com.google.common.eventbus.EventBus;

public final class DEventManager implements EventManager<EventBus> {
    private final EventBus eventBus = new EventBus();

    @Override
    public void registerListener(final Object listener) {
        this.eventBus.register(listener);
    }

    @Override
    public void unregisterListener(final Object listener) {
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
