package me.allinkdev.deviousmod.gui.widget;

import com.github.allinkdev.deviousmod.api.module.Module;
import com.github.allinkdev.deviousmod.api.module.settings.ModuleSettings;
import com.github.allinkdev.deviousmod.api.module.settings.Setting;
import imgui.ImGui;
import imgui.type.ImString;
import me.allinkdev.deviousmod.gui.AbstractImGuiLayer;

import java.io.IOException;
import java.io.UncheckedIOException;

public final class SettingsWidget extends AbstractImGuiLayer {
    private final ImString IMSTRING_TEMP = new ImString();

    private final Module module;

    public SettingsWidget(final Module module) {
        this.module = module;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process() {
        final ModuleSettings settings = this.module.getSettings();

        for (final String key : settings.getKeys()) {
            // :P
            if (key.equals("enabled")) {
                continue;
            }

            try {
                final Class<?> settingClass = settings.getValueClass(key);
                final Setting<?> value = settings.getSetting(key, settingClass);
                final String name = value.getFriendlyName() != null ? value.getFriendlyName() : value.getName();
                if (value.getValue() instanceof final Boolean bool) {
                    final boolean clicked = ImGui.checkbox(name, bool);

                    if (clicked) {
                        settings.writeValue(key, !bool, (Class<? super Boolean>) settingClass);
                    }
                } else if (value.getValue() instanceof final String string) {
                    IMSTRING_TEMP.set(string);
                    if (ImGui.inputText(name, IMSTRING_TEMP)) {
                        settings.writeValue(key, IMSTRING_TEMP.get(), (Class<? super String>) settingClass);
                    }
                }

                final boolean hovered = ImGui.isItemHovered();

                if (hovered && value.getDescription() != null) {
                    ImGui.beginTooltip();
                    System.out.println(value.getDescription());
                    ImGui.text(value.getDescription());
                    ImGui.endTooltip();
                }
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to update value of setting " + key, e);
            }
        }
    }
}
