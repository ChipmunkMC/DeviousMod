package me.allinkdev.deviousmod;

import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.command.CommandManager;
import me.allinkdev.deviousmod.event.tick.impl.ClientTickEndEvent;
import me.allinkdev.deviousmod.event.tick.impl.ClientTickStartEvent;
import me.allinkdev.deviousmod.event.tick.world.impl.WorldTickEndEvent;
import me.allinkdev.deviousmod.event.tick.world.impl.WorldTickStartEvent;
import me.allinkdev.deviousmod.keybind.KeyBindManager;
import me.allinkdev.deviousmod.keying.BotKeyProvider;
import me.allinkdev.deviousmod.module.ModuleManager;
import me.allinkdev.deviousmod.util.TextUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

public final class DeviousMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Devious Mod");
    public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final Component MESSAGE_PREFIX = Component.text("[", NamedTextColor.YELLOW)
            .append(Component.text("DeviousMod Message", NamedTextColor.GOLD))
            .append(Component.text("]", NamedTextColor.YELLOW))
            .append(Component.space());
    private static DeviousMod INSTANCE;
    private final EventBus eventBus = new EventBus();
    private final KeyBindManager keyBindManager = new KeyBindManager(this);
    private final ModuleManager moduleManager = new ModuleManager(this);
    private final CommandManager commandManager = new CommandManager(this);
    private final BotKeyProvider botKeyProvider = new BotKeyProvider();

    public static DeviousMod getInstance() {
        return INSTANCE;
    }

    public static boolean isDevelopment() {
        final RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        final List<String> inputArguments = bean.getInputArguments();

        return inputArguments.stream().anyMatch(s -> s.startsWith("-agentlib:jdwp"));
    }

    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public BotKeyProvider getBotKeyProvider() {
        return this.botKeyProvider;
    }

    public EventBus getEventBus() {
        return this.eventBus;
    }

    @Override
    public void onInitializeClient() {
        INSTANCE = this;

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

    public void sendMessage(final Component component) {
        final Text text = TextUtil.toText(MESSAGE_PREFIX.append(component));
        final ClientPlayerEntity player = CLIENT.player;

        if (player == null) {
            return;
        }

        player.sendMessage(text, false);
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
