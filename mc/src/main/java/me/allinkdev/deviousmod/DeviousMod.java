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
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

public class DeviousMod implements ModInitializer {
    public static final Logger logger = LoggerFactory.getLogger("deviousmod");
    public static final MinecraftClient client = MinecraftClient.getInstance();
    private static DeviousMod instance;
    @Getter
    private final EventBus eventBus = new EventBus();
    @Getter
    private final ModuleManager moduleManager = new ModuleManager(this);
    @Getter
    private final CommandManager commandManager = new CommandManager(this);

    public static DeviousMod getInstance() {
        return instance;
    }

    public static boolean isDevelopment() {
        final RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        final List<String> inputArguments = bean.getInputArguments();

        return inputArguments.stream().anyMatch(s -> s.startsWith("-agentlib:jdwp"));
    }

    @Override
    public void onInitialize() {
        instance = this;

        ClientCommandRegistrationCallback.EVENT.register(commandManager::register);

        ClientTickEvents.START_CLIENT_TICK.register(ClientTickStartEvent::onStartTick);
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEndEvent::onTickEnd);

        ClientTickEvents.START_WORLD_TICK.register(WorldTickStartEvent::onWorldTickStart);
        ClientTickEvents.START_WORLD_TICK.register(WorldTickEndEvent::onWorldTickEnd);
    }

    public void subscribeEvents(final Object object) {
        eventBus.register(object);
    }

    public void unsubscribeEvents(final Object object) {
        eventBus.unregister(object);
    }
}
