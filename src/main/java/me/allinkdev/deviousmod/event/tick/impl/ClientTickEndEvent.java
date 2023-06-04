package me.allinkdev.deviousmod.event.tick.impl;

import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.tick.GenericClientTickEvent;
import net.minecraft.client.MinecraftClient;

public final class ClientTickEndEvent extends GenericClientTickEvent {
    public ClientTickEndEvent(final MinecraftClient client) {
        super(client);
    }

    public static void onTickEnd(final MinecraftClient client) {
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventBus eventBus = deviousMod.getEventBus();
        final ClientTickEndEvent event = new ClientTickEndEvent(client);

        eventBus.post(event);
    }
}
