package me.allinkdev.deviousmod.query;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.network.connection.ConnectionEndEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionStartEvent;
import me.allinkdev.deviousmod.event.network.packet.impl.PacketS2CEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.QueryBlockNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryEntityNbtC2SPacket;
import net.minecraft.network.packet.s2c.play.NbtQueryResponseS2CPacket;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public final class QueryManager {
    private static final AtomicInteger QUERY_ID = new AtomicInteger(0);
    private static final Map<Integer, CompletableFuture<NbtCompound>> QUERY_MAP = new ConcurrentHashMap<>();
    private static final Queue<Packet<?>> PACKET_QUEUE = new ConcurrentLinkedQueue<>();
    private static final Timer timer = new Timer();
    private static QueryManager INSTANCE;
    private static boolean connectionOpen = false;

    public static void init(final DeviousMod deviousMod) {
        INSTANCE = new QueryManager();

        timer.schedule(new PacketSender(), 0L, 4L);
        EventUtil.registerListener(INSTANCE);
    }

    public static CompletableFuture<NbtCompound> scheduleBlockQuery(final BlockPos blockPos) {
        if (!connectionOpen) {
            return CompletableFuture.failedFuture(new IllegalStateException("Not connected!"));
        }

        final MinecraftClient client = DeviousMod.CLIENT;
        final ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();

        if (networkHandler == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("ClientPlayNetworkHandler null!"));
        }


        final int transactionId = getQueryId();
        final CompletableFuture<NbtCompound> future = new CompletableFuture<>();

        QUERY_MAP.put(transactionId, future);

        final QueryBlockNbtC2SPacket packet = new QueryBlockNbtC2SPacket(transactionId, blockPos);
        PACKET_QUEUE.offer(packet);

        return future;
    }

    public static CompletableFuture<NbtCompound> scheduleEntityQuery(final Entity entity) {
        if (!connectionOpen) {
            return CompletableFuture.failedFuture(new IllegalStateException("Not connected!"));
        }

        final MinecraftClient client = DeviousMod.CLIENT;
        final ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();

        if (networkHandler == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("ClientPlayNetworkHandler null!"));
        }


        final int transactionId = getQueryId();
        final CompletableFuture<NbtCompound> future = new CompletableFuture<>();

        QUERY_MAP.put(transactionId, future);

        final int entityId = entity.getId();
        final QueryEntityNbtC2SPacket packet = new QueryEntityNbtC2SPacket(transactionId, entityId);
        PACKET_QUEUE.offer(packet);

        return future;
    }

    private static int getQueryId() {
        return QUERY_ID.decrementAndGet();
    }

    private static void completeQuery(final int transactionId, final NbtCompound compound) {
        if (!QUERY_MAP.containsKey(transactionId)) {
            return;
        }

        final CompletableFuture<NbtCompound> future = QUERY_MAP.get(transactionId);
        future.complete(compound);
    }

    @Subscribe
    private void onConnectionEnd(final ConnectionEndEvent event) {
        connectionOpen = false;

        QUERY_MAP.values().forEach(k -> k.completeExceptionally(new IllegalStateException("Connection ended")));
        QUERY_MAP.clear();

        QUERY_ID.set(0);
    }

    @Subscribe
    private void onConnectionStart(final ConnectionStartEvent event) {
        connectionOpen = true;
    }

    @Subscribe
    private void onPacketReceive(final PacketS2CEvent event) {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof final NbtQueryResponseS2CPacket responseS2CPacket) {
            final int transactionId = responseS2CPacket.getTransactionId();
            final NbtCompound response = responseS2CPacket.getNbt();

            completeQuery(transactionId, response);
        }
    }

    private static final class PacketSender extends TimerTask {
        @Override
        public void run() {
            final ClientPlayerEntity player = DeviousMod.CLIENT.player;

            if (player == null) {
                return;
            }

            final boolean hasPerms = player.hasPermissionLevel(2);

            if (!hasPerms) {
                return;
            }

            final Packet<?> packet = PACKET_QUEUE.poll();

            if (packet == null) {
                return;
            }

            final MinecraftClient client = DeviousMod.CLIENT;
            final ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();

            if (networkHandler == null) {
                PACKET_QUEUE.clear();
                return;
            }


            networkHandler.sendPacket(packet);
        }
    }
}
