package me.allinkdev.deviousmod.event.tick.impl;

import me.allinkdev.deviousmod.event.tick.GenericClientTickEvent;
import net.minecraft.client.MinecraftClient;

public final class ClientTickEndEvent extends GenericClientTickEvent {
    public ClientTickEndEvent(final MinecraftClient client) {
        super(client);
    }

    public static void onTickEnd(final MinecraftClient client) {
        final ClientTickEndEvent event = new ClientTickEndEvent(client);

        eventBus.post(event);
    }
}
