package me.allinkdev.deviousmod.event.tick.impl;

import lombok.Getter;
import me.allinkdev.deviousmod.event.tick.ClientTickEvent;
import me.allinkdev.deviousmod.event.tick.TickEventDataContainer;
import net.minecraft.client.MinecraftClient;

@Getter
public class ClientTickEndEvent extends ClientTickEvent {

    public ClientTickEndEvent(TickEventDataContainer data) {
        super(data);
    }

    public static void onEndTick(final MinecraftClient client) {
        final TickEventDataContainer data = getContainer(client);
        final ClientTickEndEvent event = new ClientTickEndEvent(data);
        eventBus.post(event);
    }
}
