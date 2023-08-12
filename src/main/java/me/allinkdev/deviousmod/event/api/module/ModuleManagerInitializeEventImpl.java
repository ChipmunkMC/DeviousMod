package me.allinkdev.deviousmod.event.api.module;

import com.github.allinkdev.deviousmod.api.event.impl.module.ModuleManagerInitializeEvent;
import com.github.allinkdev.deviousmod.api.managers.ModuleManager;
import com.github.allinkdev.deviousmod.api.module.Module;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class ModuleManagerInitializeEventImpl implements ModuleManagerInitializeEvent {
    private final Set<Function<ModuleManager, Module>> newModuleFactories = new HashSet<>();

    public Collection<Module> createModules(final ModuleManager moduleManager) {
        return this.newModuleFactories.stream().map(m -> m.apply(moduleManager)).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void addModule(final Function<ModuleManager, Module> moduleCreationMethod) {
        this.newModuleFactories.add(moduleCreationMethod);
    }
}
