package me.allinkdev.deviousmod.gui.layer.setting;

import com.github.allinkdev.deviousmod.api.module.Module;
import me.allinkdev.deviousmod.gui.AbstractImGuiLayer;

import java.util.function.Consumer;

public abstract class AbstractModuleSettingLayer<T> extends AbstractImGuiLayer {
    protected final String name;
    protected final Module module;
    protected final Consumer<T> updateConsumer;
    protected T value;

    protected AbstractModuleSettingLayer(final String name, final Module module, final Consumer<T> updateConsumer, final T value) {
        this.name = name;
        this.module = module;
        this.updateConsumer = updateConsumer;
        this.value = value;
    }
}
