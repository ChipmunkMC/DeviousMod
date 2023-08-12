package me.allinkdev.deviousmod.module;

import com.github.allinkdev.deviousmod.api.experiments.Experimentality;
import com.github.allinkdev.deviousmod.api.managers.EventManager;
import com.github.allinkdev.deviousmod.api.managers.ModuleManager;
import com.github.allinkdev.deviousmod.api.module.Module;
import com.github.allinkdev.deviousmod.api.module.ModuleLifecycle;
import com.github.allinkdev.reflector.Reflector;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.api.factory.module.ModuleLifecycleTransitionEventFactory;
import me.allinkdev.deviousmod.event.api.module.ModuleManagerInitializeEventImpl;
import me.allinkdev.deviousmod.keybind.DKeyBindManager;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DModuleManager implements ModuleManager {
    private static final Set<Module> modules = new HashSet<>();
    private static final ModuleLifecycleTransitionEventFactory TRANSITION_EVENT_FACTORY = new ModuleLifecycleTransitionEventFactory();
    private final DeviousMod deviousMod;
    private final EventManager<?> eventManager;

    public DModuleManager(final DeviousMod deviousMod) {
        this.deviousMod = deviousMod;
        this.eventManager = deviousMod.getEventManager();

        final ModuleManagerInitializeEventImpl event = new ModuleManagerInitializeEventImpl();
        eventManager.broadcastEvent(event);

        final List<Module> newModules = Stream.concat(Reflector.createNew(DModule.class)
                .allSubClassesInSubPackage("impl")
                .map(Reflector::createNew)
                .map(r -> r.create(this))
                .map(Optional::orElseThrow), event.getNewModules().stream())
                .filter(m -> m.getExperimentality() != Experimentality.HIDE || (m.getExperimentality() == Experimentality.HIDE && DeviousMod.IS_EXPERIMENTAL))
                .toList();

        modules.addAll(newModules);

        DeviousMod.LOGGER.info("Loaded {} modules!", modules.size());
        final DKeyBindManager keyBindManager = (DKeyBindManager) this.deviousMod.getKeyBindManager();

        for (final Module module : modules) {
            initModule(module, keyBindManager);

            this.updateModuleState(module, module.getModuleState(), false);
        }
    }

    @Deprecated(forRemoval = true)
    public static Set<String> getModuleNamesStatically() {
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final DModuleManager moduleManager = deviousMod.getModuleManager();

        return moduleManager.getModuleNames();
    }

    public static void postLifecycleUpdate(final EventManager<?> eventManager, final ModuleLifecycle to, final Module module) {
        TRANSITION_EVENT_FACTORY.create(module, to, eventManager::broadcastEvent).join();
    }

    private void postLifecycleUpdate(final ModuleLifecycle to, final Module module) {
        postLifecycleUpdate(this.eventManager, to, module);
    }

    public Set<String> getModuleNames() {
        return modules.stream()
                .map(Module::getModuleName)
                .collect(Collectors.toUnmodifiableSet());
    }

    private void checkLoaded() {
        if (modules.size() < 1) {
            throw new IllegalStateException("Modules not initialised yet!");
        }
    }

    public void initModule(final Module module, final DKeyBindManager keyBindManager) {
        final GenericModuleKeyBind genericModuleKeyBind = new GenericModuleKeyBind(this.deviousMod, module);

        keyBindManager.register(genericModuleKeyBind);

        this.postLifecycleUpdate(ModuleLifecycle.REGISTERED, module);
    }

    @Override
    public void load(final Module module) {
        module.init();
        this.postLifecycleUpdate(ModuleLifecycle.INITIALIZED, module);

        deviousMod.getEventManager().registerListener(module);
        this.postLifecycleUpdate(ModuleLifecycle.LOADED, module);
    }

    @Override
    public void unload(final Module module) {
        deviousMod.getEventManager().unregisterListener(module);

        this.postLifecycleUpdate(ModuleLifecycle.UNLOADED, module);
    }

    public void updateModuleState(final Module module, final boolean newState, final boolean shouldBroadcast) {
        module.setModuleState(newState);
        if (shouldBroadcast) module.notifyModuleStateUpdate(newState);

        if (newState) {
            module.onEnable();
            this.load(module);
            DModuleManager.postLifecycleUpdate(this.eventManager, ModuleLifecycle.ENABLED, module);
        } else {
            module.onDisable();
            this.unload(module);
            DModuleManager.postLifecycleUpdate(this.eventManager, ModuleLifecycle.DISABLED, module);
        }
    }

    @Override
    public Set<Module> getModules() {
        checkLoaded();

        return Collections.unmodifiableSet(modules);
    }

    @Override
    public Optional<Module> findModule(final CharSequence name) {
        checkLoaded();

        return modules.stream()
                .filter(m -> m.getModuleName().equalsIgnoreCase((String) name))
                .findFirst();
    }

    public DeviousMod getDeviousMod() {
        return this.deviousMod;
    }
}