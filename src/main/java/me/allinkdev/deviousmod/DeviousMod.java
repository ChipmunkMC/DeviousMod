package me.allinkdev.deviousmod;

import com.github.allinkdev.deviousmod.api.DeviousModSilhouette;
import com.github.allinkdev.deviousmod.api.event.impl.imgui.ImGuiHolderOverrideEvent;
import com.github.allinkdev.deviousmod.api.gui.ImGuiHolder;
import com.github.allinkdev.deviousmod.api.load.DeviousModEntrypoint;
import com.github.allinkdev.deviousmod.api.managers.CommandManager;
import com.github.allinkdev.deviousmod.api.managers.EventManager;
import com.github.allinkdev.deviousmod.api.managers.KeyBindManager;
import me.allinkdev.deviousmod.account.AccountManager;
import me.allinkdev.deviousmod.command.DCommandManager;
import me.allinkdev.deviousmod.command.queue.CommandQueueManager;
import me.allinkdev.deviousmod.event.DEventManager;
import me.allinkdev.deviousmod.event.time.tick.impl.ClientTickEndEvent;
import me.allinkdev.deviousmod.event.time.tick.impl.ClientTickStartEvent;
import me.allinkdev.deviousmod.event.time.tick.world.impl.WorldTickEndEvent;
import me.allinkdev.deviousmod.event.time.tick.world.impl.WorldTickStartEvent;
import me.allinkdev.deviousmod.gui.DImGuiHolder;
import me.allinkdev.deviousmod.gui.ImGuiHolderProxy;
import me.allinkdev.deviousmod.keybind.DKeyBindManager;
import me.allinkdev.deviousmod.keying.BotKeyProvider;
import me.allinkdev.deviousmod.module.DModuleManager;
import me.allinkdev.deviousmod.query.QueryManager;
import me.allinkdev.deviousmod.render.RenderManager;
import me.allinkdev.deviousmod.time.Clock;
import me.allinkdev.deviousmod.util.EventUtil;
import me.allinkdev.deviousmod.util.TextUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.lenni0451.lambdaevents.LambdaManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.util.List;

public final class DeviousMod implements ClientModInitializer, DeviousModSilhouette<FabricClientCommandSource, KeyBinding, LambdaManager> {
    public static final Logger LOGGER = LoggerFactory.getLogger("Devious Mod");
    public static final MinecraftClient CLIENT = MinecraftClient.getInstance();
    public static final boolean IS_EXPERIMENTAL = ManagementFactory.getRuntimeMXBean().getInputArguments().stream().anyMatch(s -> s.startsWith("-agentlib:jdwp")) || Boolean.getBoolean("com.github.allinkdev.deviousmod.experimental");
    private static DeviousMod INSTANCE;

    private EventManager<LambdaManager> eventManager;
    private BotKeyProvider botKeyProvider;
    private DKeyBindManager keyBindManager;
    private DModuleManager moduleManager;
    private DCommandManager commandManager;
    private AccountManager accountManager;
    private RenderManager renderManager;
    private CommandQueueManager commandQueueManager;
    private ImGuiHolderProxy imGuiHolder;
    private Clock clock;

    public static DeviousMod getInstance() {
        return INSTANCE;
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
    public EventManager<LambdaManager> getEventManager() {
        return this.eventManager;
    }

    @Override
    public ImGuiHolder getImGuiHolder() {
        return this.imGuiHolder;
    }

    public AccountManager getAccountManager() {
        return this.accountManager;
    }

    public RenderManager getRenderManager() {
        return this.renderManager;
    }

    public CommandQueueManager getCommandQueueManager() {
        return this.commandQueueManager;
    }

    public Clock getClock() {
        return this.clock;
    }

    public void createImGuiHolder(final long handle) {
        this.imGuiHolder.setActualHolder(EventUtil.postEvent(new ImGuiHolderOverrideEvent()).getImGuiHolder().orElseGet(() -> new DImGuiHolder(handle)));
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
        this.imGuiHolder = new ImGuiHolderProxy();

        entrypoints.forEach(e -> e.onPreLoad(this));

        this.clock = new Clock();
        this.commandQueueManager = new CommandQueueManager();
        this.renderManager = new RenderManager();
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

    public void sendMessage(final Component component) {
        TextUtil.sendMessage(component.colorIfAbsent(NamedTextColor.GRAY));
    }

    public void sendMultipleMessages(final List<Component> messages) {
        Component builder = Component.empty();
        for (int i = 0; i < messages.size(); i++) {
            final Component message = messages.get(i);
            if (i != 0) builder = builder.append(Component.newline());
            builder = builder.append(message);
        }

        this.sendMessage(builder);
    }
}
