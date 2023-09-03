package me.allinkdev.deviousmod.settings;

import com.github.allinkdev.deviousmod.api.module.settings.Setting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DSetting<T> implements Setting<T> {
    private final String name;
    private String friendlyName;
    private String description;
    private T value;

    public DSetting(final @NotNull String name, final @NotNull T value) {
        this(name, null, null, value);
    }

    public DSetting(final String name, final @Nullable String friendlyName, final @Nullable String description, final @NotNull T value) {
        this.name = name;
        this.friendlyName = friendlyName;
        this.description = description;
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getFriendlyName() {
        return this.friendlyName;
    }

    public void setFriendlyName(final String friendlyName) {
        this.friendlyName = friendlyName;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public void setValue(final T newValue) {
        this.value = newValue;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getValueClass() {
        return (Class<T>) this.value.getClass();
    }
}
