package me.allinkdev.deviousmod.event.time.tick;

import com.github.allinkdev.deviousmod.api.event.Event;
import net.minecraft.client.MinecraftClient;

public abstract class GenericClientTickEvent implements Event {
    private final MinecraftClient client;

    protected GenericClientTickEvent(final MinecraftClient client) {
        this.client = client;
    }

    public MinecraftClient getClient() {
        return this.client;
    }
}
