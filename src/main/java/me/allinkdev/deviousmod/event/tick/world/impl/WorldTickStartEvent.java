package me.allinkdev.deviousmod.event.tick.world.impl;

import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.tick.world.GenericWorldTickEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

public final class WorldTickStartEvent extends GenericWorldTickEvent {
    public WorldTickStartEvent(final MinecraftClient client, final ClientPlayerEntity player, final ClientWorld world) {
        super(client, player, world);
    }

    public static void onWorldTickStart(final ClientWorld world) {
        final MinecraftClient client = MinecraftClient.getInstance();
        final ClientPlayerEntity player = client.player;

        if (player == null) {
            throw new IllegalStateException("Player null!");
        }

        final WorldTickStartEvent event = new WorldTickStartEvent(client, player, world);
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventBus eventBus = deviousMod.getEventBus();

        eventBus.post(event);
    }
}
