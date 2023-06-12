package me.allinkdev.deviousmod.keying;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public final class BotKeyProvider {
    public static final Path KEY_PATH = Path.of("config", "deviousmod", "settings", "keys").toAbsolutePath();
    private static final Logger LOGGER = LoggerFactory.getLogger("Bot Key Provider");
    private final Set<BotKey> loadedKeys = new HashSet<>();

    public BotKeyProvider() {
        if (!Files.exists(KEY_PATH)) {
            return;
        }

        try (final Stream<Path> pathStream = Files.list(KEY_PATH)) {
            pathStream.forEach(p -> {
                final BotKey botKey = new BotKey(p);

                try {
                    botKey.load();
                } catch (IOException e) {
                    throw new UncheckedIOException("Failed to load bot key from path", e);
                }

                loadedKeys.add(botKey);
            });
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to list key directory", e);
        }

        final int size = this.loadedKeys.size();

        if (size == 0) {
            return;
        }

        LOGGER.info("Loaded {} bot keys into memory!", size);
    }

    public void addKey(final BotKey key) {
        for (final BotKey loadedKey : this.loadedKeys) {
            if (!loadedKey.getIdentifier().equals(key.getIdentifier())) {
                continue;
            }

            throw new IllegalStateException("There is already a key with this identifier!");
        }

        loadedKeys.add(key);

        try {
            key.save();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to save key", e);
        }
    }

    public void removeKey(final BotKey key) {
        LOGGER.info("Removing key {}!", key);
        loadedKeys.remove(key);

        try {
            key.delete();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to delete key", e);
        }
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
