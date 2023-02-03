package me.allinkdev.deviousmod.event.tick;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.allinkdev.deviousmod.event.Event;
import net.minecraft.client.MinecraftClient;

@Getter
@RequiredArgsConstructor
public abstract class GenericClientTickEvent extends Event {
    private final MinecraftClient client;
}
