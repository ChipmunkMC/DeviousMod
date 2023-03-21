package me.allinkdev.deviousmod.util;

import it.unimi.dsi.fastutil.Pair;
import lombok.NonNull;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.UUID;

public final class TimeUtil extends NoConstructor {
    private static final Logger logger = LoggerFactory.getLogger("Time Util");
    private static final long BASELINE_COMMAND_DELAY = 70L;
    private static Pair<Long, Long> currentCommandDelay = Pair.of(0L, 0L);

    public static long getInTicks(final long ms) {
        return ms / 20L;
    }

    public static void invalidateCommandDelay() {
        currentCommandDelay = Pair.of(0L, 0L);
        logger.info("Command delay invalidated!");
    }

    public static long calculateCommandDelay(@NonNull final ClientPlayNetworkHandler networkHandler) {
        final long now = System.currentTimeMillis();
        final long cachedCommandDelayTimestamp = currentCommandDelay.first();

        if ((now - cachedCommandDelayTimestamp) < 60_000) {
            return currentCommandDelay.second();
        }

        final Collection<PlayerListEntry> playerList = networkHandler.getPlayerList();
        final UUID myUuid = UUIDUtil.getSelfUUID();
        final PlayerListEntry playerListEntry = playerList.stream()
                .filter(entry -> entry.getProfile().getId().equals(myUuid))
                .findFirst()
                .orElseThrow();

        final long delay = Math.min(BASELINE_COMMAND_DELAY + playerListEntry.getLatency(), 200);

        currentCommandDelay = Pair.of(now, delay);

        logger.info("Lazily calculated current command delay to be {}ms.", delay);

        return delay;
    }
}
