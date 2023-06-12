package me.allinkdev.deviousmod.gui.layer.setting;

import com.github.allinkdev.deviousmod.api.module.Module;

import java.util.function.Consumer;

@FunctionalInterface
public interface AbstractModuleSettingFactory<T, U extends AbstractModuleSettingLayer<T>> {
    U create(final String name, final Module module, final Consumer<T> updateConsumer, final T value);
}
