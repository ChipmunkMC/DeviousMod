package me.allinkdev.deviousmod.event.time.tick.impl;

import me.allinkdev.deviousmod.event.time.tick.GenericClientTickEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.client.MinecraftClient;

public final class ClientTickStartEvent extends GenericClientTickEvent {
    public ClientTickStartEvent(final MinecraftClient client) {
        super(client);
    }

    public static void onStartTick(final MinecraftClient client) {
        EventUtil.postEvent(new ClientTickStartEvent(client));
    }
}
