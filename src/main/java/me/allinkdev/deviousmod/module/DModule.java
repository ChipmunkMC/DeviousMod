package me.allinkdev.deviousmod.module;

import com.github.allinkdev.deviousmod.api.managers.EventManager;
import com.github.allinkdev.deviousmod.api.module.Module;
import com.github.allinkdev.deviousmod.api.module.ModuleLifecycle;
import com.github.allinkdev.deviousmod.api.module.settings.ModuleSettings;
import me.allinkdev.deviousmod.DeviousMod;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

public abstract class DModule extends Module {
    protected final Logger logger = LoggerFactory.getLogger("Devious Mod/" + this.getClass().getSimpleName());
    protected final DeviousMod deviousMod;
    protected final MinecraftClient client = DeviousMod.CLIENT;
    private final DModuleManager moduleManager;
    private final EventManager<?> eventManager;
    protected volatile DModuleSettings settings;

    protected DModule(final DModuleManager moduleManager) {
        super();
        this.moduleManager = moduleManager;
        this.deviousMod = moduleManager.getDeviousMod();
        this.eventManager = this.deviousMod.getEventManager();
        this.settings = this.getSettingsBuilder().build();

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
        DModuleManager.postLifecycleUpdate(this.eventManager, ModuleLifecycle.INITIALIZED, this);
    }

    protected DModuleSettings.Builder getSettingsBuilder() {
        return new DModuleSettings.Builder()
                .setPath(Path.of("modules", this.getModuleName().toLowerCase() + ".json"))
                .addField("enabled", "", "", false);
    }

    @Override
    public ModuleSettings getSettings() {
        return this.settings;
    }

    @Override
    public boolean getModuleState() {
        return this.settings.getSetting("enabled", Boolean.class).getValue();
    }

    @Override
    public void setModuleState(final boolean newState) {
        try {
            this.settings.writeValue("enabled", newState, Boolean.class);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write module enabled state", e);
        }

        this.notifyModuleStateUpdate(true);
    }

    public void notifyModuleStateUpdate(final boolean shouldBroadcast) {
        final boolean state = this.getModuleState();

        if (state) {
            this.onEnable();
            moduleManager.load(this);
            DModuleManager.postLifecycleUpdate(this.eventManager, ModuleLifecycle.ENABLED, this);
        } else {
            this.onDisable();
            moduleManager.unload(this);
            DModuleManager.postLifecycleUpdate(this.eventManager, ModuleLifecycle.DISABLED, this);
        }

        if (!shouldBroadcast) return;
        final String moduleName = this.getModuleName();
        final Component feedback = Component.text(moduleName)
                .append(Component.text(" is now "))
                .append(getStatusComponent(state))
                .append(Component.text("."));

        deviousMod.sendMessage(feedback);

    }

    public void toggle() {
        final boolean currentState = this.getModuleState();

        this.setModuleState(!currentState);
    }

    protected void sendMessage(final Component component) {
        deviousMod.sendMessage(component);
    }

    protected void sendMultipleMessages(final List<Component> components) {
        deviousMod.sendMultipleMessages(components);
    }
}
