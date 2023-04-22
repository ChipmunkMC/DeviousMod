package me.allinkdev.deviousmod.data;

import me.allinkdev.deviousmod.util.NoConstructor;

import java.nio.file.Path;

public final class Config extends NoConstructor {
    private static final Path configDirectory = Path.of(".", "config", "deviousmod");
    private static final DataCompound settings = new DataCompound("config", configDirectory, Path.of("deviousmod"));

    public static Path getConfigDirectory() {
        return configDirectory;
    }

    public static DataCompound getSettings() {
        return settings;
    }
}
