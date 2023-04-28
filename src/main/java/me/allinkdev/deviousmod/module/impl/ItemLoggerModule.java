package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import com.mojang.authlib.GameProfile;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.entity.living.impl.LivingEntityEquipmentUpdateEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.ModuleManager;
import me.allinkdev.deviousmod.util.IteratorUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public final class ItemLoggerModule extends DModule {
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private static final Path ROOT_PATH = Path.of("logs", "deviousmod", "nbt");

    static {
        final AtomicInteger count = new AtomicInteger();

        try {
            scanDirectoryForLocksAndRemove(count, ROOT_PATH);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to clear locks", e);
        }

        final int lockCount = count.get();

        if (lockCount > 0) {
            DeviousMod.LOGGER.warn("Cleared {} locks!", lockCount);
        }
    }

    public ItemLoggerModule(final ModuleManager moduleManager) {
        super(moduleManager);
    }

    private static void processPath(final AtomicInteger count, final Path path) throws IOException {
        if (Files.isDirectory(path)) {
            scanDirectoryForLocksAndRemove(count, path);
        }

        if (path.endsWith(".lock")) {
            Files.deleteIfExists(path);
            count.incrementAndGet();
        }
    }


    private static void scanDirectoryForLocksAndRemove(final AtomicInteger count, final Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }

        final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path);
        final Iterator<Path> pathIterator = directoryStream.iterator();
        final List<Path> paths = IteratorUtil.toList(pathIterator);
        directoryStream.close();

        for (final Path childPath : paths) {
            processPath(count, childPath);
        }
    }

    @Override
    public String getCategory() {
        return "Networking";
    }

    @Override
    public String getModuleName() {
        return "ItemLogger";
    }

    @Override
    public String getDescription() {
        return "Logs every player equipment update with custom NBT to disk.";
    }

    @Subscribe
    public void onEquipmentUpdate(final LivingEntityEquipmentUpdateEvent event) {
        final LivingEntity livingEntity = event.getLivingEntity();

        if (!(livingEntity instanceof final PlayerEntity playerEntity)) {
            return;
        }

        final GameProfile gameProfile = playerEntity.getGameProfile();
        final ItemStack itemStack = event.getNewStack();
        final UUID uuid = gameProfile.getId();
        final ItemStack copy = itemStack.copy();

        final SaveThread saveThread = new SaveThread(copy, uuid);
        EXECUTOR.submit(saveThread);
    }

    private static final class SaveThread extends Thread {
        private final ItemStack itemStack;
        private final UUID uuid;

        public SaveThread(final ItemStack itemStack, final UUID uuid) {
            this.itemStack = itemStack;
            this.uuid = uuid;
        }

        private static String getFileName() {
            final Instant now = Instant.now();
            final long nowMillis = now.toEpochMilli();
            final int nanos = now.getNano();
            final String name = nowMillis + "." + nanos + ".txt";

            DeviousMod.LOGGER.info("{}", name);
            return name;
        }

        private Path getPath(final UUID uuid) {
            final String stringifiedUuid = uuid.toString();

            return ROOT_PATH.resolve(stringifiedUuid);
        }

        private void createDirectories(final Path uuidPath) throws IOException {
            if (Files.exists(uuidPath)) {
                return;
            }

            final Path absoluteUuidPath = uuidPath.toAbsolutePath();
            final String absoluteUuidPathStr = absoluteUuidPath.toString();

            DeviousMod.LOGGER.info("Creating new NBT log directory at path {}!", absoluteUuidPathStr);
            Files.createDirectories(uuidPath);
        }


        private boolean hasMeta(final ItemStack itemStack) {
            final boolean hasTag = itemStack.hasNbt();

            if (!hasTag) {
                return false;
            }

            final NbtCompound nbtCompound = itemStack.getNbt();

            if (nbtCompound == null) {
                return false;
            }

            final Set<String> keys = nbtCompound.getKeys();
            final int keyAmount = keys.size();

            if (keyAmount == 0) {
                return false;
            }

            final boolean hasDamage = keys.contains("Damage");

            if (!hasDamage) {
                return true;
            }

            boolean justDamage = true;

            for (final String key : keys) {
                if (key.equals("Damage")) {
                    continue;
                }

                justDamage = false;
            }

            return !justDamage;
        }

        @Override
        public void run() {
            final boolean hasMeta = this.hasMeta(this.itemStack);

            if (!hasMeta) {
                return;
            }

            final Path path = this.getPath(this.uuid);

            try {
                this.createDirectories(path);
            } catch (IOException e) {
                DeviousMod.LOGGER.error("Failed to create NBT log directory!", e);
                return;
            }

            // We want to implement locks as we're using more than one thread. It would be bad if we attempted to write to the same file in parallel!

            final String fileName = getFileName();
            final String lock = fileName + ".lock";

            final Path filePath = path.resolve(fileName);
            final Path lockPath = path.resolve(lock);

            if (Files.exists(lockPath)) {
                DeviousMod.LOGGER.warn("Waiting for lock to be lifted on {}!", filePath);

                while (Files.exists(lockPath)) {
                    try {
                        Thread.sleep(100L);
                    } catch (InterruptedException e) {
                        DeviousMod.LOGGER.warn("Interrupted while waiting for lock to be lifted!");
                        this.interrupt();
                        return;
                    }
                }

                DeviousMod.LOGGER.info("Lock lifted on {}!", filePath);
            }

            try {
                Files.createFile(lockPath);
            } catch (IOException e) {
                DeviousMod.LOGGER.error("Failed to create lock file!", e);
                return;
            }

            final NbtCompound compound = itemStack.getNbt();

            if (compound == null) {
                DeviousMod.LOGGER.warn("Compound somehow null in save thread!");
                return;
            }

            final String stringifiedNbt = compound.asString();

            try {
                Files.writeString(filePath, stringifiedNbt, StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE);
            } catch (IOException e) {
                DeviousMod.LOGGER.warn("Failed to write to log file!", e);
                return;
            }

            // Release the lock.
            try {
                Files.deleteIfExists(lockPath);
            } catch (IOException e) {
                DeviousMod.LOGGER.warn("Failed to release the lock!", e);
            }
        }
    }
}
