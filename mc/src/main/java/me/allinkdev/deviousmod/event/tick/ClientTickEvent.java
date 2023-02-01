package me.allinkdev.deviousmod.event.tick;

import lombok.RequiredArgsConstructor;
import me.allinkdev.deviousmod.event.Event;
import net.minecraft.client.MinecraftClient;

@RequiredArgsConstructor
public abstract class ClientTickEvent extends Event {
    private final TickEventDataContainer data;

    public static TickEventDataContainer getContainer(final MinecraftClient client) {
        return new TickEventDataContainer(client);
    }
}
