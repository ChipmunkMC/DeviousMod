package me.allinkdev.deviousmod.event.api.keybind;

import com.github.allinkdev.deviousmod.api.event.impl.keybind.KeyBindLifecycleTransitionEvent;
import com.github.allinkdev.deviousmod.api.keybind.KeyBind;
import com.github.allinkdev.deviousmod.api.keybind.KeyBindLifecycle;
import net.minecraft.client.option.KeyBinding;

public record KeybindLifecycleTransitionEventImpl(KeyBind<KeyBinding> keyBind, KeyBindLifecycle from,
                                                  KeyBindLifecycle to) implements KeyBindLifecycleTransitionEvent<KeyBinding> {
    @Override
    public KeyBind<KeyBinding> getTracked() {
        return this.keyBind;
    }

    @Override
    public KeyBindLifecycle getFrom() {
        return this.from;
    }

    @Override
    public KeyBindLifecycle getTo() {
        return this.to;
    }
}
