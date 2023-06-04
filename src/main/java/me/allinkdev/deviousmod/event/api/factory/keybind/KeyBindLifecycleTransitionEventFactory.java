package me.allinkdev.deviousmod.event.api.factory.keybind;

import com.github.allinkdev.deviousmod.api.event.factory.LifecycleTransitionEventFactory;
import com.github.allinkdev.deviousmod.api.event.impl.keybind.KeyBindLifecycleTransitionEvent;
import com.github.allinkdev.deviousmod.api.keybind.KeyBind;
import com.github.allinkdev.deviousmod.api.keybind.KeyBindLifecycle;
import me.allinkdev.deviousmod.event.api.keybind.KeybindLifecycleTransitionEventImpl;
import net.minecraft.client.option.KeyBinding;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class KeyBindLifecycleTransitionEventFactory implements LifecycleTransitionEventFactory<KeyBindLifecycle, KeyBind<KeyBinding>, KeyBindLifecycleTransitionEvent<KeyBinding>> {
    @Override
    public CompletableFuture<Void> create(final KeyBind<KeyBinding> instance, final KeyBindLifecycle to, final Consumer<KeyBindLifecycleTransitionEvent<KeyBinding>> consumer) {
        return CompletableFuture.supplyAsync(() -> {
            instance.getTrackedLifecycle(l -> consumer.accept(new KeybindLifecycleTransitionEventImpl(instance, l, to))).join();
            instance.setTrackedLifecycle(to).join();

            return null;
        });
    }
}
