package me.allinkdev.deviousmod;

import com.google.common.eventbus.EventBus;
import lombok.Getter;
import me.allinkdev.deviousmod.command.CommandManager;
import me.allinkdev.deviousmod.event.tick.impl.ClientTickEndEvent;
import me.allinkdev.deviousmod.event.tick.impl.ClientTickStartEvent;
import me.allinkdev.deviousmod.event.tick.world.impl.WorldTickEndEvent;
import me.allinkdev.deviousmod.event.tick.world.impl.WorldTickStartEvent;
import me.allinkdev.deviousmod.module.ModuleManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviousMod implements ModInitializer {
    public static final Logger logger = LoggerFactory.getLogger("deviousmod");
    private static DeviousMod instance;
    @Getter
    private final EventBus eventBus = new EventBus();

    public static DeviousMod getInstance() {
        return instance;
    }

    @Override
    public void onInitialize() {
        instance = this;

        CommandManager.init();
        ModuleManager.init();

        ClientCommandRegistrationCallback.EVENT.register(CommandManager::register);

        ClientTickEvents.START_CLIENT_TICK.register(ClientTickStartEvent::onStartTick);
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEndEvent::onEndTick);

        ClientTickEvents.START_WORLD_TICK.register(WorldTickStartEvent::onStartTick);
        ClientTickEvents.START_WORLD_TICK.register(WorldTickEndEvent::onEndTick);
    }

    public void subscribeEvents(final Object object) {
        eventBus.register(object);
    }

    public void unsubscribeEvents(final Object object) {
        eventBus.unregister(object);
    }
}
