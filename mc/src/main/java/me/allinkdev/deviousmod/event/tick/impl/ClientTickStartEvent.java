package me.allinkdev.deviousmod.event.tick.impl;

import me.allinkdev.deviousmod.event.tick.ClientTickEvent;
import me.allinkdev.deviousmod.event.tick.TickEventDataContainer;
import net.minecraft.client.MinecraftClient;

public class ClientTickStartEvent extends ClientTickEvent {

    public ClientTickStartEvent(TickEventDataContainer data) {
        super(data);
    }

    public static void onStartTick(final MinecraftClient client) {
        final TickEventDataContainer container = getContainer(client);
        final ClientTickStartEvent event = new ClientTickStartEvent(container);
        eventBus.post(event);
    }
}
