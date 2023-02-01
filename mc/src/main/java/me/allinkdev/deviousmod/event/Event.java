package me.allinkdev.deviousmod.event;

import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;

public abstract class Event {
    protected static final DeviousMod deviousMod = DeviousMod.getInstance();
    protected static final EventBus eventBus = deviousMod.getEventBus();
}
