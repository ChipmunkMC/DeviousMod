package me.allinkdev.deviousmod.event.time.tick.world.impl;

import me.allinkdev.deviousmod.event.time.tick.world.GenericWorldTickEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

public final class WorldTickEndEvent extends GenericWorldTickEvent {
    public WorldTickEndEvent(final MinecraftClient client, final ClientPlayerEntity player, final ClientWorld world) {
        super(client, player, world);
    }

    public static void onWorldTickEnd(final ClientWorld world) {
        final MinecraftClient client = MinecraftClient.getInstance();
        final ClientPlayerEntity player = client.player;

        if (player == null) {
            throw new IllegalStateException("Player null!");
        }

        EventUtil.postEvent(new WorldTickEndEvent(client, player, world));
    }
}
