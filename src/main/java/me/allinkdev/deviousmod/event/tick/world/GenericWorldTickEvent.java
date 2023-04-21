package me.allinkdev.deviousmod.event.tick.world;

import lombok.RequiredArgsConstructor;
import me.allinkdev.deviousmod.event.Event;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

@RequiredArgsConstructor
public class GenericWorldTickEvent extends Event {
    private final MinecraftClient client;
    private final ClientPlayerEntity player;
    private final ClientWorld world;
}
