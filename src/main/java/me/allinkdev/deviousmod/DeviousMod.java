package me.allinkdev.deviousmod;

import com.github.allinkdev.deviousmod.api.DeviousModSilhouette;
import com.github.allinkdev.deviousmod.api.load.DeviousModEntrypoint;
import com.github.allinkdev.deviousmod.api.managers.CommandManager;
import com.github.allinkdev.deviousmod.api.managers.EventManager;
import com.github.allinkdev.deviousmod.api.managers.KeyBindManager;
import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.account.AccountManager;
import me.allinkdev.deviousmod.command.DCommandManager;
import me.allinkdev.deviousmod.event.DEventManager;
import me.allinkdev.deviousmod.event.tick.impl.ClientTickEndEvent;
import me.allinkdev.deviousmod.event.tick.impl.ClientTickStartEvent;
import me.allinkdev.deviousmod.event.tick.world.impl.WorldTickEndEvent;
import me.allinkdev.deviousmod.event.tick.world.impl.WorldTickStartEvent;
import me.allinkdev.deviousmod.keybind.DKeyBindManager;
import me.allinkdev.deviousmod.keying.BotKeyProvider;
import me.allinkdev.deviousmod.module.DModuleManager;
import me.allinkdev.deviousmod.query.QueryManager;
import me.allinkdev.deviousmod.util.TextUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

public final class DeviousMod implements ClientModInitializer, DeviousModSilhouette<FabricClientCommandSource, KeyBinding, EventBus> {
    public static final Logger LOGGER = LoggerFactory.getLogger("Devious Mod");
    public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    private static final boolean IS_DEVELOPMENT;
    private static DeviousMod INSTANCE;

    static {
        final RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        final List<String> inputArguments = bean.getInputArguments();

        IS_DEVELOPMENT = inputArguments.stream().anyMatch(s -> s.startsWith("-agentlib:jdwp"));
    }

    private EventManager<EventBus> eventManager;
    private BotKeyProvider botKeyProvider;
    private DKeyBindManager keyBindManager;
    private DModuleManager moduleManager;
    private DCommandManager commandManager;
    private AccountManager accountManager;

    public static DeviousMod getInstance() {
        return INSTANCE;
    }

    public static boolean isDevelopment() {
        return IS_DEVELOPMENT;
    }

    @Override
    public DModuleManager getModuleManager() {
        return this.moduleManager;
    }

    @Override
    public CommandManager<FabricClientCommandSource> getCommandManager() {
        return this.commandManager;
    }

    public BotKeyProvider getBotKeyProvider() {
        return this.botKeyProvider;
    }

    @Override
    public KeyBindManager<KeyBinding> getKeyBindManager() {
        return this.keyBindManager;
    }

    @Override
    public EventManager<EventBus> getEventManager() {
        return this.eventManager;
    }

    public AccountManager getAccountManager() {
        return this.accountManager;
    }

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        DeviousModSilhouette.setInstance(this);
        final FabricLoader fabricLoader = FabricLoader.getInstance();
        final List<DeviousModEntrypoint> entrypoints = fabricLoader.getEntrypointContainers("deviousmod", DeviousModEntrypoint.class)
                .stream()
                .map(EntrypointContainer::getEntrypoint)
                .toList();

        this.eventManager = new DEventManager();

        entrypoints.forEach(e -> e.onPreLoad(this));

        this.accountManager = new AccountManager(CLIENT, this);
        this.botKeyProvider = new BotKeyProvider();
        this.keyBindManager = new DKeyBindManager(this);
        this.moduleManager = new DModuleManager(this);
        this.commandManager = new DCommandManager(this);

        QueryManager.init(this);

        ClientCommandRegistrationCallback.EVENT.register(commandManager::register);

        ClientTickEvents.START_CLIENT_TICK.register(ClientTickStartEvent::onStartTick);
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEndEvent::onTickEnd);

        ClientTickEvents.START_WORLD_TICK.register(WorldTickStartEvent::onWorldTickStart);
        ClientTickEvents.START_WORLD_TICK.register(WorldTickEndEvent::onWorldTickEnd);
        entrypoints.forEach(e -> e.onLoad(this));
    }

    public EventBus getEventBus() {
        return this.eventManager.getInternalEventSystem();
    }

    @Deprecated(forRemoval = true)
    public void subscribeEvents(final Object object) {
        this.eventManager.registerListener(object);
    }

    @Deprecated(forRemoval = true)
    public void unsubscribeEvents(final Object object) {
        this.eventManager.unregisterListener(object);
    }

    public void sendMessage(final Component component) {
        final Component coloredComponent = component.colorIfAbsent(NamedTextColor.GRAY);
        final Text text = TextUtil.toText(coloredComponent);
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
