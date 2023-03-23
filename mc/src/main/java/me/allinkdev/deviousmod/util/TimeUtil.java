package me.allinkdev.deviousmod.util;

import com.google.common.eventbus.Subscribe;
import it.unimi.dsi.fastutil.Pair;
import lombok.NonNull;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.network.connection.ConnectionEndEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.UUID;

public final class TimeUtil {
    private static final Logger logger = LoggerFactory.getLogger("Time Util");
    private static final long BASELINE_COMMAND_DELAY = 70L;
    private static Pair<Long, Long> currentCommandDelay = Pair.of(0L, 0L);

    static {
        DeviousMod.getInstance().subscribeEvents(new TimeUtil());
    }

    public static long getInTicks(final long ms) {
        return ms / 20L;
    }

    public static long calculateCommandDelay(@NonNull final ClientPlayNetworkHandler networkHandler) {
        final ServerInfo serverInfo = networkHandler.getServerInfo();

        if (serverInfo == null) {
            return BASELINE_COMMAND_DELAY;
        }

        if (serverInfo.isLocal()) {
            return BASELINE_COMMAND_DELAY;
        }

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

        final long delay = (long) MathHelper.clamp(BASELINE_COMMAND_DELAY + playerListEntry.getLatency(), BASELINE_COMMAND_DELAY, 200);

        currentCommandDelay = Pair.of(now, delay);

        logger.info("Lazily calculated current command delay to be {}ms.", delay);

        return delay;
    }

    @Subscribe
    public void onConnectionEnd(final ConnectionEndEvent event) {
        currentCommandDelay = Pair.of(0L, 0L);
        logger.info("Command delay invalidated!");
    }
}
