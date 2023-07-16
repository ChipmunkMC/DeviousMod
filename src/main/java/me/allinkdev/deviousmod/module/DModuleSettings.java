package me.allinkdev.deviousmod.module;

import com.github.allinkdev.deviousmod.api.module.settings.ModuleSettings;
import com.github.allinkdev.deviousmod.api.module.settings.Setting;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.settings.AbstractDataStore;
import me.allinkdev.deviousmod.settings.DSetting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class DModuleSettings extends AbstractDataStore implements ModuleSettings {
    private final Map<String, DSetting<Object>> settings;

    DModuleSettings(final @NotNull Path path, final Map<String, DSetting<Object>> settings) {
        super(path);

        this.settings = settings;
    }

    public static Builder builder() {
        return new Builder();
    }

    private void addToJsonObject(final JsonObject jsonObject, final String key, final DSetting<?> value) {
        final JsonElement jsonElement = GSON.toJsonTree(value.getValue());
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

            final DSetting<Object> settingData = this.settings.get(key);
            final Class<?> settingClass = settingData.getValueClass();
            final JsonElement element = jsonObject.get(key);
            final Object asObject = GSON.fromJson(element, settingClass);

            if (!(settingClass.isInstance(asObject))) {
                DeviousMod.LOGGER.warn("Found field ({}) with invalid type, ignoring...", key);
                continue;
            }

            settingData.setValue(asObject);
        }
    }

    public void load() throws IOException {
        load(DModuleSettings.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Setting<T> getSetting(final String name, final Class<T> clazz) {
        final DSetting<?> setting = this.settings.get(name);

        if (setting == null) throw new NullPointerException("Tried to read a field that didn't exist: " + name);
        final Class<?> actualClass = setting.getValueClass();

        if (!(actualClass.isAssignableFrom(clazz)) && !(clazz.isAssignableFrom(actualClass)))
            throw new IllegalArgumentException("Tried to get a field of an invalid type, expected " + clazz.getName() + " when we actually got " + actualClass.getName());
        return (Setting<T>) setting;
    }

    @Override
    public <T> void writeValue(final String name, final T object, final Class<T> clazz) throws IOException {
        final DSetting<Object> setting = this.settings.get(name);

        if (setting == null) throw new NullPointerException("Tried to set a field that doesn't exist: " + name);
        final Class<?> actualClass = setting.getValueClass();

        if (!(actualClass.isAssignableFrom(clazz)) && !(clazz.isAssignableFrom(actualClass)))
            throw new IllegalArgumentException("Tried to set a field of an invalid type, expected " + clazz.getName() + " when we actually got " + actualClass.getName());
        setting.setValue(object);
        this.save();
    }

    @Override
    public Class<?> getValueClass(final String name) {
        final DSetting<?> setting = this.settings.get(name);
        if (setting == null) throw new NullPointerException("Tried to query the class of a field that didn't exist: " + name);
        return setting.getValueClass();
    }

    @Override
    public Set<String> getKeys() {
        return Collections.unmodifiableSet(this.settings.keySet());
    }

    public static final class Builder {
        private final Map<String, DSetting<Object>> objectMap = new Object2ObjectArrayMap<>();
        private @Nullable Path path;

        private void throwIfExists(final String name) {
            if (!objectMap.containsKey(name)) return;
            throw new IllegalArgumentException("Tried to add a field with the same name twice!");
        }

        private DSetting<Object> getAsSetting(final @NotNull Object defaultValue, final @NotNull String name, final @NotNull String friendlyName, final @Nullable String description, final @NotNull Class<?> clazz) {
            return new DSetting<>(name, friendlyName, description, defaultValue);
        }

        public Builder addField(final @NotNull String name, final @NotNull String friendlyName, final @NotNull String description, final @NotNull Object defaultValue) {
            this.throwIfExists(name);
            this.objectMap.put(name, this.getAsSetting(defaultValue, name, friendlyName, description, defaultValue.getClass()));
            return this;
        }

        public Builder setPath(final @NotNull Path path) {
            this.path = path;
            return this;
        }

        public DModuleSettings build() {
            if (this.path == null) throw new IllegalArgumentException("Tried to construct a module settings instance without first setting a path!");
            return new DModuleSettings(this.path, this.objectMap);
        }
    }
}