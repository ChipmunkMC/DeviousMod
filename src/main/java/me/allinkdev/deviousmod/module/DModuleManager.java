package me.allinkdev.deviousmod.module;

import com.github.allinkdev.deviousmod.api.managers.EventManager;
import com.github.allinkdev.deviousmod.api.managers.ModuleManager;
import com.github.allinkdev.deviousmod.api.module.Module;
import com.github.allinkdev.deviousmod.api.module.ModuleLifecycle;
import com.github.allinkdev.deviousmod.api.module.settings.ModuleSettings;
import com.github.allinkdev.reflector.Reflector;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.api.factory.module.ModuleLifecycleTransitionEventFactory;
import me.allinkdev.deviousmod.event.api.module.ModuleManagerInitializeEventImpl;
import me.allinkdev.deviousmod.keybind.DKeyBindManager;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class DModuleManager implements ModuleManager {
    private static final ModuleLifecycleTransitionEventFactory TRANSITION_EVENT_FACTORY = new ModuleLifecycleTransitionEventFactory();
    private final Set<Module> modules = new HashSet<>();
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
                        .map(Optional::orElseThrow), event.createModules(this).stream())
                .filter(m -> DeviousMod.isPermittedExperimentality(m.getExperimentality()))
                .toList();

        this.modules.addAll(newModules);

        DeviousMod.LOGGER.info("Loaded {} modules!", this.modules.size());
        final DKeyBindManager keyBindManager = (DKeyBindManager) this.deviousMod.getKeyBindManager();

        for (final Module module : this.modules) {
            initModule(module, keyBindManager);

            this.updateModuleState(module, module.getModuleState(), false);
        }
    }

    public static void postLifecycleUpdate(final EventManager<?> eventManager, final ModuleLifecycle to, final Module module) {
        TRANSITION_EVENT_FACTORY.create(module, to, eventManager::broadcastEvent).join();
    }

    private void postLifecycleUpdate(final ModuleLifecycle to, final Module module) {
        postLifecycleUpdate(this.eventManager, to, module);
    }

    public Set<String> getModuleNames() {
        return this.modules.stream()
                .map(Module::getModuleName)
                .collect(Collectors.toUnmodifiableSet());
    }

    private void checkLoaded() {
        if (this.modules.isEmpty()) {
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

        this.deviousMod.getEventManager().registerListener(module);
        this.postLifecycleUpdate(ModuleLifecycle.LOADED, module);
    }

    @Override
    public void unload(final Module module) {
        this.deviousMod.getEventManager().unregisterListener(module);

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
        this.checkLoaded();

        return Collections.unmodifiableSet(this.modules);
    }

    @Override
    public Optional<Module> findModule(final CharSequence name) {
        this.checkLoaded();

        return this.modules.stream()
                .filter(m -> m.getModuleName().equalsIgnoreCase((String) name))
                .findFirst();
    }

    @Override
    public Supplier<ModuleSettings.Builder> getModuleSettingsBuilderSupplier() {
        return DModuleSettings::builder;
    }

    public DeviousMod getDeviousMod() {
        return this.deviousMod;
    }
}