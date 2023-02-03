package me.allinkdev.deviousmod.event.tick.impl;

import me.allinkdev.deviousmod.event.tick.GenericClientTickEvent;
import net.minecraft.client.MinecraftClient;

public class ClientTickStartEvent extends GenericClientTickEvent {
    public ClientTickStartEvent(final MinecraftClient client) {
        super(client);
    }

    public static void onStartTick(final MinecraftClient client) {
        final ClientTickStartEvent event = new ClientTickStartEvent(client);

        eventBus.post(event);
    }
}
