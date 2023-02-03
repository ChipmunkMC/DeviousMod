package me.allinkdev.deviousmod.module;

import com.github.steveice10.opennbt.tag.builtin.ByteTag;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.data.DataCompound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Optional;

public abstract class DModule {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final DeviousMod deviousMod = DeviousMod.getInstance();
    protected final DataCompound settings;

    protected DModule() {
        final String moduleName = this.getModuleName();
        this.settings = new DataCompound(moduleName, ModuleManager.getModuleConfigPath(), Path.of(moduleName));
    }

    public abstract String getModuleName();

    public abstract String getDescription();

    protected Optional<ByteTag> getTag() {
        final String moduleName = getModuleName();
        final DataCompound moduleSettings = ModuleManager.getSettings();
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

        final DataCompound moduleSettings = ModuleManager.getSettings();
        final CompoundTag compoundTag = moduleSettings.getCompoundTag();

        compoundTag.put(newTag);
        moduleSettings.save();

        if (newState) {
            deviousMod.subscribeEvents(this);
            return;
        }

        deviousMod.unsubscribeEvents(this);
    }
}
