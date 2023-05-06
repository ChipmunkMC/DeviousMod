package me.allinkdev.deviousmod.module;

import com.github.allinkdev.reflector.Reflector;
import com.github.steveice10.opennbt.tag.builtin.ByteTag;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.data.Config;
import me.allinkdev.deviousmod.data.DataCompound;
import me.allinkdev.deviousmod.event.transformer.impl.Transformer;
import me.allinkdev.deviousmod.keybind.KeyBindManager;
import me.allinkdev.deviousmod.module.impl.TestModule;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static me.allinkdev.deviousmod.DeviousMod.LOGGER;

public final class ModuleManager {
    private static final Set<DModule> modules = new HashSet<>();
    private final Path moduleConfigPath = Config.getConfigDirectory();
    private final DataCompound settings = new DataCompound("modules", moduleConfigPath, Path.of("modules"));
    private final Map<DModule, Set<Transformer>> transformers = new HashMap<>();
    private final DeviousMod deviousMod;

    public ModuleManager(final DeviousMod deviousMod) {
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

        LOGGER.info("Loaded {} modules!", modules.size());
        final KeyBindManager keyBindManager = this.deviousMod.getKeyBindManager();

        for (final DModule module : modules) {
            initModule(module, keyBindManager);

            final String moduleName = module.getModuleName();
            final CompoundTag compoundTag = settings.getCompoundTag();
            final ByteTag byteTag = compoundTag.get(moduleName);

            if (byteTag == null || byteTag.getValue() == 0) {
                continue;
            }

            LOGGER.info("Enabling {}!", moduleName);
            module.setModuleState(true);
        }
    }


    public static Set<String> getModuleNames() {
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

    public void load(final DModule module) {
        module.init();
        transformers.put(module, module.getTransformers());

        deviousMod.subscribeEvents(module);
    }

    public void unload(final DModule module) {
        transformers.remove(module);

        deviousMod.unsubscribeEvents(module);
        module.clearTransformers();
    }

    /**
     * Unmodifiable
     */
    public Set<DModule> getModules() {
        checkLoaded();

        return Collections.unmodifiableSet(modules);
    }

    public Optional<DModule> findModule(final String name) {
        checkLoaded();

        return modules.stream()
                .filter(m -> m.getModuleName().equalsIgnoreCase(name))
                .findFirst();
    }

    public List<Transformer> getTransformers() {
        final List<List<Transformer>> transformerList = transformers.values().stream()
                .map(List::copyOf)
                .toList();

        final List<Transformer> flattened = new ArrayList<>();

        for (final List<Transformer> list : transformerList) {
            flattened.addAll(list);
        }

        return flattened;
    }

    @SuppressWarnings("unchecked")
    public <T extends Transformer> List<T> getTransformers(final Class<T> type) {
        final List<Transformer> allTransformers = getTransformers();

        return (List<T>) allTransformers.stream()
                .filter(t -> Arrays.stream(t.getClass().getInterfaces()).toList().contains(type))
                .toList();
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