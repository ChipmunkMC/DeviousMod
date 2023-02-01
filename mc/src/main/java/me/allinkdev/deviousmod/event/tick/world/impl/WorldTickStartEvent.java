package me.allinkdev.deviousmod.event.tick.world.impl;

import me.allinkdev.deviousmod.event.tick.world.WorldTickEvent;
import me.allinkdev.deviousmod.event.tick.world.WorldTickEventDataContainer;
import net.minecraft.client.world.ClientWorld;

public class WorldTickStartEvent extends WorldTickEvent {
    public WorldTickStartEvent(WorldTickEventDataContainer data) {
        super(data);
    }

    public static void onStartTick(final ClientWorld world) {
        final WorldTickEventDataContainer data = getContainer(world);
        final WorldTickStartEvent event = new WorldTickStartEvent(data);
        eventBus.post(event);
    }
}
