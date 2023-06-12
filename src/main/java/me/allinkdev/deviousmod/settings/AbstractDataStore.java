package me.allinkdev.deviousmod.settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractDataStore {
    protected static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setLenient()
            .create();
    private static final Path BASE_PATH = Path.of("config", "deviousmod", "settings");
    protected volatile Path path;

    protected AbstractDataStore(final @NotNull Path path) {
        this.path = path.isAbsolute() ? path : BASE_PATH.resolve(path);
    }

    public boolean doesPathExist() {
        return Files.exists(this.path);
    }

    public void createDirectories() throws IOException {
        Files.createDirectories(this.path.getParent());
    }

    public void delete() throws IOException {
        Files.deleteIfExists(this.path);
    }

    public abstract void save() throws IOException;

    public <T extends AbstractDataStore> void load(final Class<T> clazz) throws IOException {
        final JsonElement asJsonElement = GSON.fromJson(Files.readString(this.path), JsonElement.class);
        this.load(asJsonElement);
    }

    protected abstract void load(final JsonElement jsonElement) throws IOException;
}
