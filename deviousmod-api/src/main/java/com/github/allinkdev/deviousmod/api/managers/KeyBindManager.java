package com.github.allinkdev.deviousmod.api.managers;

import com.github.allinkdev.deviousmod.api.keybind.KeyBind;

import java.util.Set;

/**
 * Represents an object that is responsible for registering {@link KeyBind<T>} with Minecraft's KeyBinding system.
 */
public interface KeyBindManager<T> {
    /**
     * Register a {@link KeyBind<T>}
     *
     * @param keyBind the {@link KeyBind<T>} to register.
     */
    void register(final KeyBind<T> keyBind);

    /**
     * This method is called every tick to tell every {@link KeyBind<T>} to check if the internal {@link T} class it wraps around if it was pressed.
     */
    void onTick();

    /**
     * @return an unmodifiable set containing all {@link KeyBind<T>} associated with this {@link KeyBindManager<T>}
     */
    Set<KeyBind<T>> getKeyBinds();
}
