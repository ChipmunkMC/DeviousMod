package me.allinkdev.deviousmod.data;

import lombok.Getter;
import me.allinkdev.deviousmod.util.NoConstructor;

import java.nio.file.Path;

public final class Config extends NoConstructor {
    @Getter
    private static final Path configDirectory = Path.of(".", "config", "deviousmod");
    @Getter
    private static final DataCompound settings = new DataCompound("config", configDirectory, Path.of("deviousmod"));

}
