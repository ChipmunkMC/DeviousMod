package com.github.allinkdev.deviousmod.api.event.impl.keybind;

import com.github.allinkdev.deviousmod.api.event.impl.LifecycleTransitionEvent;
import com.github.allinkdev.deviousmod.api.keybind.KeyBind;
import com.github.allinkdev.deviousmod.api.keybind.KeyBindLifecycle;

/**
 * An event that signifies a {@link KeyBind<V>}'s transition between a {@link KeyBindLifecycle} and another.
 */
public interface KeyBindLifecycleTransitionEvent<V> extends LifecycleTransitionEvent<KeyBindLifecycle, KeyBind<V>> {

}
