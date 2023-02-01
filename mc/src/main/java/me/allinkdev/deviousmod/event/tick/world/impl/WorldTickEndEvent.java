package me.allinkdev.deviousmod.event.tick.world.impl;

import me.allinkdev.deviousmod.event.tick.world.WorldTickEvent;
import me.allinkdev.deviousmod.event.tick.world.WorldTickEventDataContainer;
import net.minecraft.client.world.ClientWorld;

public class WorldTickEndEvent extends WorldTickEvent {
    public WorldTickEndEvent(WorldTickEventDataContainer data) {
        super(data);
    }

    public static void onEndTick(final ClientWorld world) {
        final WorldTickEventDataContainer data = getContainer(world);
        final WorldTickEndEvent event = new WorldTickEndEvent(data);
        eventBus.post(event);
    }
}
