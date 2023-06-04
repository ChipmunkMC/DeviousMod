package com.github.allinkdev.deviousmod.api.event.impl;

import com.github.allinkdev.deviousmod.api.event.Event;
import com.github.allinkdev.deviousmod.api.lifecycle.Lifecycle;

/**
 * An event that marks the transition of a {@link U} from one {@link T} to another {@link T}
 *
 * @param <T> the lifecycle type
 * @param <U> the tracked object type
 */
public interface LifecycleTransitionEvent<T extends Lifecycle, U> extends Event {
    /**
     * @return the tracked {@link T} instance
     */
    U getTracked();

    /**
     * @return the {@link T} the {@link LifecycleTransitionEvent} marks the transition from
     */
    T getFrom();

    /**
     * @return the {@link T} the {@link LifecycleTransitionEvent} marks the transition to
     */
    T getTo();
}
