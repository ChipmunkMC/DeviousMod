package me.allinkdev.deviousmod.event.tick.world;

import com.github.allinkdev.deviousmod.api.event.Event;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

public class GenericWorldTickEvent implements Event {
    private final MinecraftClient client;
    private final ClientPlayerEntity player;
    private final ClientWorld world;

    public GenericWorldTickEvent(final MinecraftClient client, final ClientPlayerEntity player, final ClientWorld world) {
        this.client = client;
        this.player = player;
        this.world = world;
    }

    public MinecraftClient getClient() {
        return this.client;
    }

    public ClientPlayerEntity getPlayer() {
        return this.player;
    }

    public ClientWorld getWorld() {
        return this.world;
    }
}
