package me.allinkdev.deviousmod.event.tick.world.impl;

import me.allinkdev.deviousmod.event.Event;
import me.allinkdev.deviousmod.event.tick.world.GenericWorldTickEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

public class WorldTickEndEvent extends GenericWorldTickEvent {
    public WorldTickEndEvent(final MinecraftClient client, final ClientPlayerEntity player, final ClientWorld world) {
        super(client, player, world);
    }

    public static void onWorldTickEnd(final ClientWorld world) {
        final MinecraftClient client = Event.client;
        final ClientPlayerEntity player = client.player;

        if (player == null) {
            throw new IllegalStateException("Player null!");
        }

        final WorldTickEndEvent event = new WorldTickEndEvent(client, player, world);

        eventBus.post(event);
    }
}
