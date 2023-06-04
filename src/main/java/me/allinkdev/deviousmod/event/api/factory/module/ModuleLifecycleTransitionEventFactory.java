package me.allinkdev.deviousmod.event.api.factory.module;

import com.github.allinkdev.deviousmod.api.event.factory.LifecycleTransitionEventFactory;
import com.github.allinkdev.deviousmod.api.event.impl.module.ModuleLifecycleTransitionEvent;
import com.github.allinkdev.deviousmod.api.module.Module;
import com.github.allinkdev.deviousmod.api.module.ModuleLifecycle;
import me.allinkdev.deviousmod.event.api.module.ModuleLifecycleTransitionEventImpl;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class ModuleLifecycleTransitionEventFactory implements LifecycleTransitionEventFactory<ModuleLifecycle, Module, ModuleLifecycleTransitionEvent> {
    @Override
    public CompletableFuture<Void> create(final Module instance, final ModuleLifecycle to, final Consumer<ModuleLifecycleTransitionEvent> consumer) {
        return CompletableFuture.supplyAsync(() -> {
            instance.getTrackedLifecycle(l -> consumer.accept(new ModuleLifecycleTransitionEventImpl(instance, l, to))).join();
            instance.setTrackedLifecycle(to).join();
            return null;
        });
    }
}
