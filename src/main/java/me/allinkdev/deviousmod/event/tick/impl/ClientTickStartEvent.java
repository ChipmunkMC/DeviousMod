package me.allinkdev.deviousmod.event.tick.impl;

import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.tick.GenericClientTickEvent;
import net.minecraft.client.MinecraftClient;

public final class ClientTickStartEvent extends GenericClientTickEvent {
    public ClientTickStartEvent(final MinecraftClient client) {
        super(client);
    }

    public static void onStartTick(final MinecraftClient client) {
        final ClientTickStartEvent event = new ClientTickStartEvent(client);
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventBus eventBus = deviousMod.getEventBus();

        eventBus.post(event);
    }
}
