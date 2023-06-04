package com.github.allinkdev.deviousmod.api.event.factory;

import com.github.allinkdev.deviousmod.api.event.impl.LifecycleTransitionEvent;
import com.github.allinkdev.deviousmod.api.lifecycle.Lifecycle;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * A class that constructs instances of {@link E} which track instances of {@link U}
 *
 * @param <T> the lifecycle type
 * @param <U> the tracked object type
 * @param <E> the lifecycle transition event
 */
public interface LifecycleTransitionEventFactory<T extends Lifecycle, U, E extends LifecycleTransitionEvent<T, U>> {
    /**
     * @param instance an instance of {@link U}
     * @param to       the new lifecycle
     * @param consumer a consumer that accepts valuse of type {@link E}
     * @return a completable future which signifies the completion of the consumer
     */
    CompletableFuture<Void> create(final U instance, final T to, final Consumer<E> consumer);
}
