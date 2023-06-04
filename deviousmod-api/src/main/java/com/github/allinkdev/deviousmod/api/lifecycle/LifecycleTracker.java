package com.github.allinkdev.deviousmod.api.lifecycle;

import com.github.allinkdev.deviousmod.api.module.ModuleLifecycle;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * An interface responsible for tracking a {@link Lifecycle} value
 */
public interface LifecycleTracker<T extends Lifecycle> {
    /**
     * Awaits a read lock, and calls a {@link Consumer<ModuleLifecycle>} with the value.
     *
     * @param lifecycleConsumer a consumer that accepts instances of the lifecycle type
     * @return the completable future responsible for acquiring the read lock, and calling the provided consumer
     */
    CompletableFuture<Void> getTrackedLifecycle(final Consumer<T> lifecycleConsumer);

    /**
     * Updates the tracked lifecycle.
     *
     * @param lifecycle the new lifecycle
     * @return a completable future responsible for acquiring the write lock, and writing the value
     */
    CompletableFuture<Void> setTrackedLifecycle(final T lifecycle);
}
