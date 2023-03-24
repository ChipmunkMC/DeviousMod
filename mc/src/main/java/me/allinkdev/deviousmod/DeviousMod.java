package me.allinkdev.deviousmod;

import com.google.common.eventbus.EventBus;
import lombok.Getter;
import me.allinkdev.deviousmod.command.CommandManager;
import me.allinkdev.deviousmod.event.tick.impl.ClientTickEndEvent;
import me.allinkdev.deviousmod.event.tick.impl.ClientTickStartEvent;
import me.allinkdev.deviousmod.event.tick.world.impl.WorldTickEndEvent;
import me.allinkdev.deviousmod.event.tick.world.impl.WorldTickStartEvent;
import me.allinkdev.deviousmod.keying.BotKeyProvider;
import me.allinkdev.deviousmod.module.ModuleManager;
import me.allinkdev.deviousmod.util.TimeUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

public class DeviousMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("deviousmod");
    public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final Component MESSAGE_PREFIX = Component.text("[", NamedTextColor.YELLOW)
            .append(Component.text("DeviousMod Message", NamedTextColor.GOLD))
            .append(Component.text("]", NamedTextColor.YELLOW))
            .append(Component.space());
    private static final Audience CLIENT_AUDIENCE = FabricClientAudiences.of()
            .audience();
    private static DeviousMod INSTANCE;
    @Getter
    private final EventBus eventBus = new EventBus();
    @Getter
    private final ModuleManager moduleManager = new ModuleManager(this);
    @Getter
    private final CommandManager commandManager = new CommandManager(this);
    @Getter
    private final BotKeyProvider botKeyProvider = new BotKeyProvider();

    public static DeviousMod getINSTANCE() {
        return INSTANCE;
    }

    public static boolean isDevelopment() {
        final RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        final List<String> inputArguments = bean.getInputArguments();

        return inputArguments.stream().anyMatch(s -> s.startsWith("-agentlib:jdwp"));
    }

    @Override
    public void onInitialize() {
        INSTANCE = this;

        ClientCommandRegistrationCallback.EVENT.register(commandManager::register);

        ClientTickEvents.START_CLIENT_TICK.register(ClientTickStartEvent::onStartTick);
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEndEvent::onTickEnd);

        ClientTickEvents.START_WORLD_TICK.register(WorldTickStartEvent::onWorldTickStart);
        ClientTickEvents.START_WORLD_TICK.register(WorldTickEndEvent::onWorldTickEnd);

        TimeUtil.init(this);

        // TODO: Doesn't fix kick
        //NbtList.TYPE = new BetterNbtListReader();
    }

    public void subscribeEvents(final Object object) {
        eventBus.register(object);
    }

    public void unsubscribeEvents(final Object object) {
        eventBus.unregister(object);
    }

    public void sendMessage(final Component component) {
        CLIENT_AUDIENCE.sendMessage(MESSAGE_PREFIX.append(component));
    }

    public void sendMultipleMessages(final List<Component> messages) {
        Component builder = Component.empty();
        for (int i = 0; i < messages.size(); i++) {
            final Component message = messages.get(i);

            if (i != 0) {
                builder = builder.append(Component.newline());
            }

            builder = builder.append(message);
        }

        this.sendMessage(builder);
    }
}
