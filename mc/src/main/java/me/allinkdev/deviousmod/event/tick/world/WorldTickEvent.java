package me.allinkdev.deviousmod.event.tick.world;

import lombok.RequiredArgsConstructor;
import me.allinkdev.deviousmod.event.Event;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

@RequiredArgsConstructor
public class WorldTickEvent extends Event {
    private final WorldTickEventDataContainer data;

    public static WorldTickEventDataContainer getContainer(final ClientWorld world) {
        final MinecraftClient client = MinecraftClient.getInstance();
        final ClientPlayerEntity player = client.player;

        if (player == null) {
            throw new IllegalStateException("Player world null in tick event even though it should be non-null!");
        }

        return new WorldTickEventDataContainer(client, player, world);
    }
}
