package me.allinkdev.deviousmod.gui.layer.setting.impl;

import com.github.allinkdev.deviousmod.api.module.Module;
import imgui.ImGui;
import me.allinkdev.deviousmod.gui.layer.setting.AbstractModuleSettingLayer;

import java.util.function.Consumer;

public class BooleanModuleSettingLayer extends AbstractModuleSettingLayer<Boolean> {
    public BooleanModuleSettingLayer(final String name, final Module module, final Consumer<Boolean> updateConsumer, final Boolean value) {
        super(name, module, updateConsumer, value);
    }

    @Override
    public void process() {
        final boolean pressed = ImGui.checkbox(name, value);

        if (pressed) {
            this.value = !this.value;
            this.updateConsumer.accept(this.value);
        }
    }
}
