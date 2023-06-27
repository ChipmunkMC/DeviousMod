package me.allinkdev.deviousmod.util;

import com.github.allinkdev.deviousmod.api.event.Cancellable;
import com.github.allinkdev.deviousmod.api.event.Event;
import com.github.allinkdev.deviousmod.api.managers.EventManager;
import me.allinkdev.deviousmod.DeviousMod;

public final class EventUtil extends NoConstructor {
    public static <T extends Event> T postEvent(final T event) {
        final EventManager<?> eventManager = DeviousMod.getInstance().getEventManager();
        eventManager.broadcastEvent(event);
        return event;
    }

    public static boolean postCancellable(final Cancellable cancellable) {
        return postEvent(cancellable).isCancelled();
    }

    public static <T> void registerListener(final T t) {
        DeviousMod.getInstance().getEventManager().registerListener(t);
    }

    public static <T> void unregisterListener(final T t) {
        DeviousMod.getInstance().getEventManager().unregisterListener(t);
    }
}
