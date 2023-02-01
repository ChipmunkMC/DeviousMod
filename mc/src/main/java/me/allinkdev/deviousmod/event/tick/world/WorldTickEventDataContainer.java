package me.allinkdev.deviousmod.event.tick.world;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

public record WorldTickEventDataContainer(MinecraftClient client, ClientPlayerEntity player, ClientWorld world) {
}
