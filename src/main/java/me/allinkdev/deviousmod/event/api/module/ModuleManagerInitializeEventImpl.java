package me.allinkdev.deviousmod.event.api.module;

import com.github.allinkdev.deviousmod.api.event.impl.module.ModuleManagerInitializeEvent;
import com.github.allinkdev.deviousmod.api.module.Module;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManagerInitializeEventImpl implements ModuleManagerInitializeEvent {
    private final List<Module> newModules = new ArrayList<>();

    @Override
    public void addModule(final Module module) {
        this.newModules.add(module);
    }

    public List<Module> getNewModules() {
        return this.newModules;
    }
}
