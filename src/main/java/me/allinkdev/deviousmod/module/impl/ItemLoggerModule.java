package me.allinkdev.deviousmod.module.impl;

import com.mojang.authlib.GameProfile;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.entity.living.impl.LivingEntityEquipmentUpdateEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import net.lenni0451.lambdaevents.EventHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ItemLoggerModule extends DModule {
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private static final Path ROOT_PATH = Path.of("logs", "deviousmod", "nbt");

    public ItemLoggerModule(final DModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    public String getCategory() {
        return "Network";
    }

    @Override
    public String getModuleName() {
        return "ItemLogger";
    }

    @Override
    public String getDescription() {
        return "Logs every player equipment update with custom NBT to disk.";
    }

    @EventHandler
    public void onEquipmentUpdate(final LivingEntityEquipmentUpdateEvent event) {
        final LivingEntity livingEntity = event.getLivingEntity();
        if (!(livingEntity instanceof final PlayerEntity playerEntity)) return;
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

            return nowMillis + "." + nanos + ".txt";
        }

        private Path getPath(final UUID uuid) {
            final String stringifiedUuid = uuid.toString();

            return ROOT_PATH.resolve(stringifiedUuid);
        }

        private void createDirectories(final Path uuidPath) throws IOException {
            if (Files.exists(uuidPath)) return;
            final Path absoluteUuidPath = uuidPath.toAbsolutePath();
            final String absoluteUuidPathStr = absoluteUuidPath.toString();

            DeviousMod.LOGGER.info("Creating new NBT log directory at path {}!", absoluteUuidPathStr);
            Files.createDirectories(uuidPath);
        }


        private boolean hasMeta(final ItemStack itemStack) {
            final boolean hasTag = itemStack.hasNbt();
            if (!hasTag) return false;
            final NbtCompound nbtCompound = itemStack.getNbt();
            if (nbtCompound == null) return false;
            final Set<String> keys = nbtCompound.getKeys();
            final int keyAmount = keys.size();
            if (keyAmount == 0) return false;
            final boolean hasDamage = keys.contains("Damage");
            if (!hasDamage) return true;
            boolean justDamage = true;

            for (final String key : keys) {
                if (key.equals("Damage")) continue;
                justDamage = false;
                // TODO: Break here
            }

            return !justDamage;
        }

        @Override
        public void run() {
            final boolean hasMeta = this.hasMeta(this.itemStack);
            if (!hasMeta) return;

            final Path path = this.getPath(this.uuid);

            try {
                this.createDirectories(path);
            } catch (IOException e) {
                DeviousMod.LOGGER.error("Failed to create NBT log directory!", e);
                return;
            }

            final String fileName = getFileName();

            final Path filePath = path.resolve(fileName);
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
            }
        }
    }
}
