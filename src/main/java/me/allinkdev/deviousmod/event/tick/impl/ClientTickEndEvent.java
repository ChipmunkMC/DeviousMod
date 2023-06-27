package me.allinkdev.deviousmod.event.tick.impl;

import me.allinkdev.deviousmod.event.tick.GenericClientTickEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.client.MinecraftClient;

public final class ClientTickEndEvent extends GenericClientTickEvent {
    public ClientTickEndEvent(final MinecraftClient client) {
        super(client);
    }

    public static void onTickEnd(final MinecraftClient client) {
        EventUtil.postEvent(new ClientTickEndEvent(client));
    }
}
