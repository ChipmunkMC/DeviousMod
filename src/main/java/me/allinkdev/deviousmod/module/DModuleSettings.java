package me.allinkdev.deviousmod.module;

import com.github.allinkdev.deviousmod.api.module.settings.ModuleSettings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.settings.AbstractDataStore;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class DModuleSettings extends AbstractDataStore implements ModuleSettings {
    private final Map<String, Pair<Object, Class<?>>> settings;

    DModuleSettings(final @NotNull Path path, final Map<String, Pair<Object, Class<?>>> settings) {
        super(path);

        this.settings = settings;
    }

    public static Builder builder() {
        return new Builder();
    }

    private void addToJsonObject(final JsonObject jsonObject, final String key, final Pair<Object, Class<?>> value) {
        final JsonElement jsonElement = GSON.toJsonTree(value.getLeft());
        jsonObject.add(key, jsonElement);
    }

    @Override
    public void save() throws IOException {
        final JsonObject jsonObject = new JsonObject();
        this.settings.forEach((key, value) -> this.addToJsonObject(jsonObject, key, value));

        Files.writeString(this.path, GSON.toJson(jsonObject));
    }

    @Override
    protected void load(final JsonElement jsonElement) throws IOException {
        if (!jsonElement.isJsonObject()) {
            throw new IllegalArgumentException("Found unexpected non-JSON object in module settings!");
        }

        final JsonObject jsonObject = jsonElement.getAsJsonObject();

        for (final String key : jsonObject.keySet()) {
            if (!this.settings.containsKey(key)) {
                DeviousMod.LOGGER.warn("Found unexpected key in configuration file ({}), ignoring...", key);
                continue;
            }

            final Pair<Object, Class<?>> settingData = this.settings.get(key);
            final Class<?> settingClass = settingData.getRight();
            final JsonElement element = jsonObject.get(key);
            final Object asObject = GSON.fromJson(element, settingClass);

            if (!(settingClass.isInstance(asObject))) {
                DeviousMod.LOGGER.warn("Found field ({}) with invalid type, ignoring...", key);
                continue;
            }

            this.settings.put(key, new Pair<>(asObject, settingClass));
        }
    }

    public void load() throws IOException {
        load(DModuleSettings.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T readValue(final String name, final Class<T> clazz) {
        final Pair<Object, Class<?>> data = this.settings.get(name);

        if (data == null) {
            throw new NullPointerException("Tried to read a field that didn't exist: " + name);
        }

        final Class<?> actualClass = data.getRight();

        if (!(actualClass.isAssignableFrom(clazz)) && !(clazz.isAssignableFrom(actualClass))) {
            throw new IllegalArgumentException("Tried to get a field of an invalid type, expected " + clazz.getName() + " when we actually got " + actualClass.getName());
        }

        return (T) data.getLeft();
    }

    @Override
    public <T> void writeValue(final String name, final T object, final Class<T> clazz) throws IOException {
        final Pair<Object, Class<?>> data = this.settings.get(name);

        if (data == null) {
            throw new NullPointerException("Tried to set a field that doesn't exist: " + name);
        }

        final Class<?> actualClass = data.getRight();

        if (!(actualClass.isAssignableFrom(clazz)) && !(clazz.isAssignableFrom(actualClass))) {
            throw new IllegalArgumentException("Tried to set a field of an invalid type, expected " + clazz.getName() + " when we actually got " + actualClass.getName());
        }

        this.settings.put(name, new Pair<>(object, actualClass));
        this.save();
    }

    @Override
    public Set<String> getKeys() {
        return Collections.unmodifiableSet(this.settings.keySet());
    }

    public static final class Builder {
        private final Map<String, Pair<Object, Class<?>>> objectMap = new Object2ObjectArrayMap<>();
        private @Nullable Path path;

        private void throwIfExists(final String name) {
            if (!objectMap.containsKey(name)) {
                return;
            }

            throw new IllegalArgumentException("Tried to add a field with the same name twice!");
        }

        private Pair<Object, Class<?>> getAsPair(final @NotNull Object defaultValue, final @NotNull Class<?> clazz) {
            return new Pair<>(defaultValue, clazz);
        }

        public Builder addField(final @NotNull String name, final @NotNull Object defaultValue) {
            this.throwIfExists(name);
            this.objectMap.put(name, this.getAsPair(defaultValue, defaultValue.getClass()));
            return this;
        }

        public Builder setPath(final @NotNull Path path) {
            this.path = path;
            return this;
        }

        public DModuleSettings build() {
            if (this.path == null) {
                throw new IllegalArgumentException("Tried to construct a module settings instance without first setting a path!");
            }

            return new DModuleSettings(this.path, this.objectMap);
        }
    }
}