package me.allinkdev.deviousmod.event;

import com.google.common.eventbus.EventBus;
import lombok.Getter;
import me.allinkdev.deviousmod.DeviousMod;
import net.minecraft.client.MinecraftClient;

public abstract class Event {
    protected static final MinecraftClient client = MinecraftClient.getInstance();
    protected static final DeviousMod deviousMod = DeviousMod.getINSTANCE();
    @Getter
    protected static final EventBus eventBus = deviousMod.getEventBus();
}
