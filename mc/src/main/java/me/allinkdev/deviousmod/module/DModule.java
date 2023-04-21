package me.allinkdev.deviousmod.module;

import com.github.steveice10.opennbt.tag.builtin.ByteTag;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.data.DataCompound;
import me.allinkdev.deviousmod.event.transformer.impl.Transformer;
import net.kyori.adventure.text.Component;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

public abstract class DModule {
    protected final Logger logger = LoggerFactory.getLogger("Devious Mod/" + this.getClass().getSimpleName());
    protected final DeviousMod deviousMod;
    protected final MinecraftClient client = DeviousMod.CLIENT;
    protected final DataCompound settings;
    private final Set<Transformer> transformers = new HashSet<>();
    private final ModuleManager moduleManager;

    protected DModule(final ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
        this.deviousMod = moduleManager.getDeviousMod();

        final String moduleName = this.getModuleName();
        this.settings = new DataCompound(moduleName, moduleManager.getModuleConfigPath(), Path.of(moduleName));
    }

    /***
     * Called on world initialization and module enable
     */
    public void init() {

    }

    public abstract String getModuleName();

    public abstract String getDescription();

    protected Optional<ByteTag> getTag() {
        final String moduleName = getModuleName();
        final DataCompound moduleSettings = moduleManager.getSettings();
        final CompoundTag tag = moduleSettings.getCompoundTag();
        final ByteTag moduleStateTag = tag.get(moduleName);

        return Optional.ofNullable(moduleStateTag);
    }

    public boolean getModuleState() {
        final Optional<ByteTag> optional = getTag();

        if (optional.isEmpty()) {
            return false;
        }

        final ByteTag tag = optional.get();

        return tag.getValue() == (byte) 1;
    }

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
            return;
        }

        this.onDisable();
        moduleManager.unload(this);
    }

    public void onEnable() {
        //
    }

    public void onDisable() {
        //
    }

    public Set<Transformer> getTransformers() {
        return Collections.unmodifiableSet(transformers);
    }

    protected void addTransformer(final Transformer transformer) {
        this.transformers.add(transformer);
    }

    protected void clearTransformers() {
        this.transformers.clear();
    }

    protected void sendMessage(final Component component) {
        deviousMod.sendMessage(component);
    }

    protected void sendMultipleMessages(final List<Component> components) {
        deviousMod.sendMultipleMessages(components);
    }
}
