package me.allinkdev.deviousmod.event.tick;

import me.allinkdev.deviousmod.event.Event;
import net.minecraft.client.MinecraftClient;

public abstract class GenericClientTickEvent extends Event {
    private final MinecraftClient client;

    protected GenericClientTickEvent(final MinecraftClient client) {
        this.client = client;
    }

    public MinecraftClient getClient() {
        return this.client;
    }
}
