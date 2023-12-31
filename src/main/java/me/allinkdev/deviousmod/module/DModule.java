package me.allinkdev.deviousmod.module;

import com.github.allinkdev.deviousmod.api.module.Module;
import me.allinkdev.deviousmod.DeviousMod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public abstract class DModule extends Module {
    protected final Logger logger = LoggerFactory.getLogger("Devious Mod/" + this.getClass().getSimpleName());
    protected final DeviousMod deviousMod;
    protected final MinecraftClient client = DeviousMod.CLIENT;

    protected DModule(final DModuleManager moduleManager) {
        super(moduleManager);
        this.deviousMod = moduleManager.getDeviousMod();

        if (!this.settings.doesPathExist()) {
            try {
                this.settings.createDirectories();
            } catch (IOException e) {
                throw new UncheckedIOException("Exception while creating directories for module settings", e);
            }

            try {
                this.settings.save();
            } catch (IOException e) {
                throw new UncheckedIOException("Exception while saving defaults to module settings path", e);
            }
            return;
        }

        try {
            this.settings.load();
        } catch (IOException e) {
            throw new UncheckedIOException("Failure while loading module settings from filesystem", e);
        }
    }

    public static Component getStatusComponent(final boolean state) {
        return state ? Component.text("enabled", NamedTextColor.GREEN) : Component.text("disabled", NamedTextColor.RED);
    }

    public void init() {
    }

    public void notifyModuleStateUpdate(final boolean newState) {
        final String moduleName = this.getModuleName();
        final Component feedback = Component.text(moduleName)
                .append(Component.text(" is now "))
                .append(getStatusComponent(newState))
                .append(Component.text("."));

        deviousMod.sendMessage(feedback);
    }

    protected void sendMessage(final Component component) {
        deviousMod.sendMessage(component);
    }

    protected void sendMultipleMessages(final List<Component> components) {
        deviousMod.sendMultipleMessages(components);
    }
}
