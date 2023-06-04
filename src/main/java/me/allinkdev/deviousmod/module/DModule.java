package me.allinkdev.deviousmod.module;

import com.github.allinkdev.deviousmod.api.lifecycle.GenericLifecycleTracker;
import com.github.allinkdev.deviousmod.api.managers.EventManager;
import com.github.allinkdev.deviousmod.api.module.Module;
import com.github.allinkdev.deviousmod.api.module.ModuleLifecycle;
import com.github.steveice10.opennbt.tag.builtin.ByteTag;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.data.DataCompound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public abstract class DModule extends GenericLifecycleTracker<ModuleLifecycle> implements Module {
    protected final Logger logger = LoggerFactory.getLogger("Devious Mod/" + this.getClass().getSimpleName());
    protected final DeviousMod deviousMod;
    protected final MinecraftClient client = DeviousMod.CLIENT;
    protected final DataCompound settings;
    private final DModuleManager moduleManager;
    private final EventManager<EventBus> eventManager;

    protected DModule(final DModuleManager moduleManager) {
        super(ModuleLifecycle.NONE);
        this.moduleManager = moduleManager;
        this.deviousMod = moduleManager.getDeviousMod();
        this.eventManager = this.deviousMod.getEventManager();

        final String moduleName = this.getModuleName();
        this.settings = new DataCompound(moduleName, moduleManager.getModuleConfigPath(), Path.of(moduleName));
    }

    public static Component getStatusComponent(final boolean state) {
        return state ? Component.text("enabled", NamedTextColor.GREEN) : Component.text("disabled", NamedTextColor.RED);
    }

    public void init() {
        DModuleManager.postLifecycleUpdate(this.eventManager, ModuleLifecycle.INITIALIZED, this);
    }

    protected Optional<ByteTag> getTag() {
        final String moduleName = getModuleName();
        final DataCompound moduleSettings = moduleManager.getSettings();
        final CompoundTag tag = moduleSettings.getCompoundTag();
        final ByteTag moduleStateTag = tag.get(moduleName);

        return Optional.ofNullable(moduleStateTag);
    }

    @Override
    public boolean getModuleState() {
        final Optional<ByteTag> optional = getTag();

        if (optional.isEmpty()) {
            return false;
        }

        final ByteTag tag = optional.get();

        return tag.getValue() == (byte) 1;
    }

    @Override
    public void setModuleState(final boolean newState) {
        final String name = getModuleName();
        final ByteTag newTag = new ByteTag(name, (byte) (newState ? 1 : 0));

        final DataCompound moduleSettings = moduleManager.getSettings();
        final CompoundTag compoundTag = moduleSettings.getCompoundTag();

        compoundTag.put(newTag);
        moduleSettings.save();

        if (newState) {
            this.onEnable();
            moduleManager.load(this);
            DModuleManager.postLifecycleUpdate(this.eventManager, ModuleLifecycle.ENABLED, this);
        } else {
            this.onDisable();
            moduleManager.unload(this);
            DModuleManager.postLifecycleUpdate(this.eventManager, ModuleLifecycle.DISABLED, this);
        }

        final String moduleName = this.getModuleName();
        final Component feedback = Component.text(moduleName)
                .append(Component.text(" is now "))
                .append(getStatusComponent(newState))
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
