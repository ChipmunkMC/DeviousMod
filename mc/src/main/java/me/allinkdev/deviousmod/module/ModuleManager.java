package me.allinkdev.deviousmod.module;

import lombok.Getter;
import me.allinkdev.deviousmod.data.Config;
import me.allinkdev.deviousmod.data.DataCompound;
import me.allinkdev.deviousmod.module.impl.TestModule;
import me.allinkdev.deviousmod.util.NoConstructor;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import static me.allinkdev.deviousmod.DeviousMod.LOGGER;

public class ModuleManager extends NoConstructor {
    @Getter
    private static final Path moduleConfigPath = Config.getConfigDirectory();
    @Getter
    private static final DataCompound settings = new DataCompound("modules", moduleConfigPath, Path.of("modules"));
    private static final Set<DModule> modules = new HashSet<>();

    public static void init() {
        modules.add(new TestModule());

        LOGGER.info("Loaded {} modules!", modules.size());
    }
}
