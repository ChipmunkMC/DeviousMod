package me.allinkdev.deviousmod.event;

import com.github.allinkdev.deviousmod.api.event.Event;
import com.github.allinkdev.deviousmod.api.managers.EventManager;
import net.lenni0451.lambdaevents.LambdaManager;
import net.lenni0451.lambdaevents.generator.MethodHandleGenerator;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class DEventManager implements EventManager<LambdaManager> {
    private final LambdaManager lambdaManager = LambdaManager.threadSafe(new MethodHandleGenerator());
    private final Set<Object> listeners = Collections.synchronizedSet(new LinkedHashSet<>());

    @Override
    public void registerListener(final Object listener) {
        if (this.listeners.contains(listener)) {
            return;
        }

        this.lambdaManager.register(listener);
        this.listeners.add(listener);
    }

    @Override
    public void unregisterListener(final Object listener) {
        if (!this.listeners.contains(listener)) {
            return;
        }

        this.lambdaManager.unregister(listener);
        this.listeners.remove(listener);
    }

    @Override
    public void broadcastEvent(final Event event) {
        this.lambdaManager.call(event);
    }

    @Override
    public LambdaManager getInternalEventSystem() {
        return this.lambdaManager;
    }
}
