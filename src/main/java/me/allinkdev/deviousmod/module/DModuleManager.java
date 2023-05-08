package me.allinkdev.deviousmod.module;

import com.github.allinkdev.deviousmod.api.Module;
import com.github.allinkdev.deviousmod.api.managers.ModuleManager;
import com.github.allinkdev.reflector.Reflector;
import com.github.steveice10.opennbt.tag.builtin.ByteTag;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.data.Config;
import me.allinkdev.deviousmod.data.DataCompound;
import me.allinkdev.deviousmod.keybind.KeyBindManager;
import me.allinkdev.deviousmod.module.impl.TestModule;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public final class DModuleManager implements ModuleManager {
    private static final Set<DModule> modules = new HashSet<>();
    private final Path moduleConfigPath = Config.getConfigDirectory();
    private final DataCompound settings = new DataCompound("modules", moduleConfigPath, Path.of("modules"));
    private final DeviousMod deviousMod;

    public DModuleManager(final DeviousMod deviousMod) {
        this.deviousMod = deviousMod;

        deviousMod.subscribeEvents(this);

        final List<? extends DModule> newModules = Reflector.createNew(DModule.class)
                .allSubClassesInSubPackage("impl")
                .map(Reflector::createNew)
                .map(r -> r.create(this))
                .map(Optional::orElseThrow)
                .filter(m -> !(m instanceof TestModule) || DeviousMod.isDevelopment())
                .toList();

        modules.addAll(newModules);

        DeviousMod.LOGGER.info("Loaded {} modules!", modules.size());
        final KeyBindManager keyBindManager = this.deviousMod.getKeyBindManager();

        for (final DModule module : modules) {
            initModule(module, keyBindManager);

            final String moduleName = module.getModuleName();
            final CompoundTag compoundTag = settings.getCompoundTag();
            final ByteTag byteTag = compoundTag.get(moduleName);

            if (byteTag == null || byteTag.getValue() == 0) {
                continue;
            }

            DeviousMod.LOGGER.info("Enabling {}!", moduleName);
            module.setModuleState(true);
        }
    }

    @Deprecated(forRemoval = true)
    public static Set<String> getModuleNamesStatically() {
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final DModuleManager moduleManager = deviousMod.getModuleManager();

        return moduleManager.getModuleNames();
    }

    public Set<String> getModuleNames() {
        return modules.stream()
                .map(DModule::getModuleName)
                .collect(Collectors.toUnmodifiableSet());
    }

    private void checkLoaded() {
        if (modules.size() < 1) {
            throw new IllegalStateException("Modules not initialised yet!");
        }
    }

    public void initModule(final DModule module, final KeyBindManager keyBindManager) {
        final GenericModuleKeyBind genericModuleKeyBind = new GenericModuleKeyBind(this.deviousMod, module);

        keyBindManager.register(genericModuleKeyBind);
    }

    @Override
    public void load(final Module module) {
        module.init();

        deviousMod.subscribeEvents(module);
    }

    @Override
    public void unload(final Module module) {
        deviousMod.unsubscribeEvents(module);
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
                .map(m -> (Module) m)
                .findFirst();
    }

    public DeviousMod getDeviousMod() {
        return this.deviousMod;
    }

    public DataCompound getSettings() {
        return this.settings;
    }

    public Path getModuleConfigPath() {
        return this.moduleConfigPath;
    }
}