package com.github.allinkdev.deviousmod.api.lifecycle;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * A generic tracker for a {@link Lifecycle} implementatio.
 *
 * @param <T> the tracked {@link Lifecycle} implementation
 */
public class GenericLifecycleTracker<T extends Lifecycle> implements LifecycleTracker<T> {
    private final ReentrantReadWriteLock lifecycleReadWriteLock = new ReentrantReadWriteLock();
    private T lifecycle;

    /**
     * Initializes a {@link GenericLifecycleTracker<T>}
     *
     * @param initialValue the initial lifecycle value to be mutated from
     */
    public GenericLifecycleTracker(final T initialValue) {
        this.lifecycle = initialValue;
    }

    @Override
    public CompletableFuture<Void> getTrackedLifecycle(final Consumer<T> lifecycleConsumer) {
        return CompletableFuture.supplyAsync(() -> {
            final ReentrantReadWriteLock.ReadLock readLock = this.lifecycleReadWriteLock.readLock();
            readLock.lock();

            lifecycleConsumer.accept(this.lifecycle);

            readLock.unlock();
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> setTrackedLifecycle(final T lifecycle) {
        return CompletableFuture.supplyAsync(() -> {
            final ReentrantReadWriteLock.WriteLock writeLock = this.lifecycleReadWriteLock.writeLock();
            writeLock.lock();

            this.lifecycle = lifecycle;

            writeLock.unlock();
            return null;
        });
    }
}
