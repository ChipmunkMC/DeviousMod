package me.allinkdev.deviousmod.data;

import com.github.steveice10.opennbt.NBTIO;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import lombok.Getter;
import me.allinkdev.deviousmod.data.exception.DataLoadException;
import me.allinkdev.deviousmod.data.exception.DataSaveException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Getter
public final class DataCompound {
    private final CompoundTag compoundTag;
    private final String compoundName;
    private final Path location;
    private final File file;

    public DataCompound(final String compoundName, final Path directory, final Path fileName) {
        this.compoundName = compoundName;
        this.location = directory.resolve(fileName);
        this.file = location.toFile();

        if (!this.file.exists()) {
            this.compoundTag = new CompoundTag(compoundName);
            return;
        }

        try {
            this.compoundTag = NBTIO.readFile(this.file, true, true);
        } catch (IOException e) {
            throw new DataLoadException(this.file, e);
        }
    }

    public void save() {
        try {
            NBTIO.writeFile(this.compoundTag, this.file, true, true);
        } catch (IOException e) {
            throw new DataSaveException(this.file, e);
        }
    }
}
