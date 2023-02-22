package me.allinkdev.deviousmod.module;

import com.github.steveice10.opennbt.tag.builtin.ByteTag;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import lombok.Getter;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.data.Config;
import me.allinkdev.deviousmod.data.DataCompound;
import me.allinkdev.deviousmod.event.transformer.impl.Transformer;
import me.allinkdev.deviousmod.module.impl.*;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static me.allinkdev.deviousmod.DeviousMod.logger;

public class ModuleManager {
    private static final Set<DModule> modules = new HashSet<>();
    @Getter
    private final Path moduleConfigPath = Config.getConfigDirectory();
    @Getter
    private final DataCompound settings = new DataCompound("modules", moduleConfigPath, Path.of("modules"));
    private final Map<DModule, Set<Transformer>> transformers = new HashMap<>();
    @Getter
    private final DeviousMod deviousMod;

    public ModuleManager(final DeviousMod deviousMod) {
        this.deviousMod = deviousMod;

        deviousMod.subscribeEvents(this);

        if (DeviousMod.isDevelopment()) {
            modules.add(new TestModule(this));
        }

        modules.add(new KeepCorpsesModule(this));
        modules.add(new ClientsideInventoryModule(this));
        modules.add(new LogosModule(this));
        modules.add(new DisableParticlesModule(this));

        logger.info("Loaded {} modules!", modules.size());

        for (final DModule module : modules) {
            final String moduleName = module.getModuleName();
            final CompoundTag compoundTag = settings.getCompoundTag();
            final ByteTag byteTag = compoundTag.get(moduleName);

            if (byteTag == null) {
                continue;
            }

            if (byteTag.getValue() == 0) {
                continue;
            }

            logger.info("Enabling {}!", module.getModuleName());
            initModule(module);
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

    public void initModule(final DModule module) {
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
}