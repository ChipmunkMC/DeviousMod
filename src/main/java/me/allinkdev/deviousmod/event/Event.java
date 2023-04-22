package me.allinkdev.deviousmod.event;

import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;
import net.minecraft.client.MinecraftClient;

public abstract class Event {
    protected static final MinecraftClient client = DeviousMod.CLIENT;
    protected static final DeviousMod deviousMod = DeviousMod.getInstance();
    protected static final EventBus eventBus = deviousMod.getEventBus();

    public static DeviousMod getDeviousMod() {
        return deviousMod;
    }

    public static EventBus getEventBus() {
        return eventBus;
    }
}
