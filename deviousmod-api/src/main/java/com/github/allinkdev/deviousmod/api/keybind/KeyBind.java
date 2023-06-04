package com.github.allinkdev.deviousmod.api.keybind;

import com.github.allinkdev.deviousmod.api.lifecycle.LifecycleTracker;
import com.github.allinkdev.deviousmod.api.managers.KeyBindManager;

/**
 * A class that represents a key binding.
 */
public interface KeyBind<T> extends LifecycleTracker<KeyBindLifecycle> {
    /**
     * @return the keycode that is associated to this {@link KeyBind<T>} by default.
     */
    int getDefaultKey();

    /**
     * @return the name of the {@link KeyBind<T>}, as shown in the controls screen
     */
    String getName();

    /**
     * The method that is called by the {@link KeyBindManager<T>} when the {@link KeyBind<T>} is pressed
     */
    void onPress();

    /**
     * The method that is called by the {@link KeyBindManager<T>} when the {@link KeyBind<T>} is no longer pressed
     */
    void onDepress();

    /**
     * @return the internal {@link T} this {@link KeyBind<T>} wraps around
     */
    Class<T> getInternal();
}
