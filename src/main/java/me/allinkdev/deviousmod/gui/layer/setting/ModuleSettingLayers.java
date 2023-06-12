package me.allinkdev.deviousmod.gui.layer.setting;

import com.github.allinkdev.deviousmod.api.module.Module;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import me.allinkdev.deviousmod.gui.layer.setting.impl.BooleanModuleSettingLayer;

import java.util.Map;
import java.util.function.Consumer;

public final class ModuleSettingLayers {
    private static final Map<Class<?>, AbstractModuleSettingFactory<Object, AbstractModuleSettingLayer<Object>>> SETTING_LAYER_MAP = new Object2ObjectArrayMap<>();

    static {
        register(Boolean.class, (name, module, updateConsumer, value) -> new BooleanModuleSettingLayer(name, module, updateConsumer, value));
    }

    public static <T> void register(final Class<T> clazz, final AbstractModuleSettingFactory<Object, AbstractModuleSettingLayer<Object>> moduleSettingFactory) {
        if (SETTING_LAYER_MAP.containsKey(clazz)) {
            throw new IllegalStateException("Tried to register a module setting layer that already exists!");
        }

        SETTING_LAYER_MAP.put(clazz, moduleSettingFactory);
    }

    @SuppressWarnings("unchecked")
    public static <T> AbstractModuleSettingLayer<T> getModuleSettingLayer(final String name, final Module module, final Consumer<T> updateConsumer, final T value, final Class<T> clazz) {
        final AbstractModuleSettingFactory<Object, AbstractModuleSettingLayer<Object>> moduleSettingFactory = SETTING_LAYER_MAP.get(clazz);

        if (moduleSettingFactory == null) {
            throw new IllegalStateException("Failed to find module settings layer for type " + clazz.getName() + "!");
        }

        return (AbstractModuleSettingLayer<T>) moduleSettingFactory.create(name, module, (Consumer<Object>) updateConsumer, value);
    }
}
