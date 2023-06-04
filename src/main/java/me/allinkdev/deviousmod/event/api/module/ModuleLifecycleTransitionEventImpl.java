package me.allinkdev.deviousmod.event.api.module;

import com.github.allinkdev.deviousmod.api.event.impl.module.ModuleLifecycleTransitionEvent;
import com.github.allinkdev.deviousmod.api.module.Module;
import com.github.allinkdev.deviousmod.api.module.ModuleLifecycle;

public record ModuleLifecycleTransitionEventImpl(Module module, ModuleLifecycle from,
                                                 ModuleLifecycle to) implements ModuleLifecycleTransitionEvent {

    @Override
    public Module getTracked() {
        return this.module;
    }

    @Override
    public ModuleLifecycle getFrom() {
        return this.from;
    }

    @Override
    public ModuleLifecycle getTo() {
        return this.to;
    }
}
