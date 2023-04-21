package me.allinkdev.deviousmod.keying;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.builtin.ListTag;
import com.github.steveice10.opennbt.tag.builtin.Tag;
import me.allinkdev.deviousmod.data.Config;
import me.allinkdev.deviousmod.data.DataCompound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class BotKeyProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger("Bot Key Provider");

    private final DataCompound compound;
    private final CompoundTag compoundTag;
    private final ListTag listTag;
    private final Set<BotKey> loadedKeys = new HashSet<>();

    public BotKeyProvider() {
        this.compound = new DataCompound("Keys", Config.getConfigDirectory(), Path.of("botKeys"));

        this.compoundTag = compound.getCompoundTag();

        ListTag listTag = this.compoundTag.get("keys");

        if (listTag == null) {
            listTag = new ListTag("keys");
        }

        this.compoundTag.put(listTag);
        this.compound.save();

        this.listTag = listTag;

        for (final Tag tag : listTag) {
            if (!(tag instanceof final CompoundTag compoundTag)) {
                throw new IllegalArgumentException("Invalid data in keys list!");
            }

            final BotKey botKey = BotKey.loadFromTag(compoundTag);
            loadedKeys.add(botKey);
        }

        final int size = this.loadedKeys.size();

        if (size == 0) {
            return;
        }

        LOGGER.info("Loaded {} bot keys into memory!", size);
    }

    public void addKey(final BotKey key) {
        loadedKeys.add(key);

        final CompoundTag compoundTag = key.getAsTag();
        listTag.add(compoundTag);

        this.compound.save();
    }

    public void removeKey(final BotKey key) {
        LOGGER.info("Removing key {}!", key);
        loadedKeys.remove(key);

        final CompoundTag compoundTag = key.getAsTag();
        listTag.remove(compoundTag);

        this.compound.save();
    }

    public Optional<BotKey> findKey(final String name) {
        return loadedKeys.stream()
                .filter(k -> k.getIdentifier().equalsIgnoreCase(name))
                .findAny();
    }

    public Set<BotKey> getLoadedKeys() {
        return Collections.unmodifiableSet(loadedKeys);
    }
}
