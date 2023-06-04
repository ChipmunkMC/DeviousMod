package com.github.allinkdev.deviousmod.api.keybind;

import com.github.allinkdev.deviousmod.api.lifecycle.Lifecycle;

/**
 * A class that represents the lifecycle of a {@link KeyBind}
 */
public enum KeyBindLifecycle implements Lifecycle {
    /**
     * The keybind has not been registered.
     */
    NONE,
    /**
     * The keybind has been registered.
     */
    REGISTERED,
    /**
     * The keybind has been pressed.
     */
    PRESSED,
    /**
     * The keybind has been depressed.
     */
    DEPRESSED;

    @Override
    public Lifecycle getDefault() {
        return NONE;
    }
}
