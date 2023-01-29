package me.allinkdev.deviousmod.module;

import com.github.steveice10.opennbt.tag.builtin.ByteTag;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import me.allinkdev.deviousmod.data.DataCompound;

import java.nio.file.Path;
import java.util.Optional;

public abstract class DModule {
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

    public void setModuleState(boolean newState) {
        final String name = getModuleName();
        final ByteTag newTag = new ByteTag(name, (byte) (newState ? 1 : 0));

        final DataCompound moduleSettings = ModuleManager.getSettings();
        final CompoundTag compoundTag = moduleSettings.getCompoundTag();

        compoundTag.put(newTag);
        moduleSettings.save();
    }
}
