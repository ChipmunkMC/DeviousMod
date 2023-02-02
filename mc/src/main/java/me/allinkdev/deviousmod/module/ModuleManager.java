package me.allinkdev.deviousmod.module;

import com.github.steveice10.opennbt.tag.builtin.ByteTag;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import lombok.Getter;
import me.allinkdev.deviousmod.data.Config;
import me.allinkdev.deviousmod.data.DataCompound;
import me.allinkdev.deviousmod.module.impl.KeepCorpsesModule;
import me.allinkdev.deviousmod.module.impl.TestModule;
import me.allinkdev.deviousmod.util.NoConstructor;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static me.allinkdev.deviousmod.DeviousMod.logger;

public class ModuleManager extends NoConstructor {
    @Getter
    private static final Path moduleConfigPath = Config.getConfigDirectory();
    @Getter
    private static final DataCompound settings = new DataCompound("modules", moduleConfigPath, Path.of("modules"));
    private static final Set<DModule> modules = new HashSet<>();

    public static void init() {
        modules.clear();
        modules.add(new TestModule());
        modules.add(new KeepCorpsesModule());

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
            module.setModuleState(true);
        }
    }

    private static void checkLoaded() {
        if (modules.size() < 1) {
            throw new IllegalStateException("Modules not initialised yet!");
        }
    }

    /**
     * Unmodifiable
     */
    public static Set<DModule> getModules() {
        checkLoaded();

        return Collections.unmodifiableSet(modules);
    }

    public static Set<String> getModuleNames() {
        final Set<DModule> modules = getModules();

        return modules.stream()
                .map(DModule::getModuleName)
                .collect(Collectors.toUnmodifiableSet());
    }

    public static Optional<DModule> findModule(final String name) {
        checkLoaded();

        return modules.stream()
                .filter(m -> m.getModuleName().equalsIgnoreCase(name))
                .findFirst();
    }
}