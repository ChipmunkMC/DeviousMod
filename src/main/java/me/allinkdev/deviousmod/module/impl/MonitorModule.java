package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.network.connection.ConnectionEndEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionStartEvent;
import me.allinkdev.deviousmod.event.network.packet.impl.PacketS2CEvent;
import me.allinkdev.deviousmod.event.tick.world.impl.WorldTickEndEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import me.allinkdev.deviousmod.util.TextUtil;
import me.allinkdev.deviousmod.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class MonitorModule extends DModule {
    private static final long TEN_SECOND_MARK_TICKS = TimeUtil.getInTicks(10_000);
    private final Set<ListEntryStub> entryStubs = new LinkedHashSet<>();
    private long worldTickCount = 0;
    private long lastTimePacket = System.currentTimeMillis();
    private boolean notifiedAboutLag = false;

    public MonitorModule(final DModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    public String getCategory() {
        return "Debugging";
    }

    @Override
    public String getModuleName() {
        return "Monitor";
    }

    @Override
    public String getDescription() {
        return "Monitors server state information such as player gamemodes and server performance information.";
    }

    @Subscribe
    public void onPacketS2C(final PacketS2CEvent event) {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof WorldTimeUpdateS2CPacket) {
            this.lastTimePacket = System.currentTimeMillis();
            return;
        }

        if (packet instanceof final PlayerRemoveS2CPacket removePacket) {
            removePacket.profileIds().forEach(u -> this.entryStubs.removeIf(p -> p.getProfile().getId().equals(u)));
            return;
        }

        if (!(packet instanceof final PlayerListS2CPacket playerListPacket)) {
            return;
        }

        final EnumSet<PlayerListS2CPacket.Action> actions = playerListPacket.getActions();
        final List<Component> messages = new ObjectArrayList<>();

        for (final PlayerListS2CPacket.Entry entry : playerListPacket.getEntries()) {
            final Set<RecordableChange> changes = new ObjectArraySet<>();
            final ListEntryStub stub;
            final UUID uuid = entry.profileId();

            if (actions.contains(PlayerListS2CPacket.Action.ADD_PLAYER)) {
                stub = ListEntryStub.from(entry);
                this.entryStubs.add(stub);
            } else {
                stub = this.entryStubs.stream().filter(s -> s.getProfile().getId().equals(uuid)).findFirst().orElseGet(() -> {
                    final ListEntryStub newStub = ListEntryStub.from(entry);
                    this.entryStubs.add(newStub);
                    return newStub;
                });
            }

            if (actions.contains(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME)) {
                final Pair<Component, Component> setDisplayName = stub.setDisplayName(Objects.requireNonNull(entry.displayName(), "Display name was null in display name update"));
                final Component oldDisplayName = setDisplayName.first();
                final Component newDisplayName = setDisplayName.second();

                if (!oldDisplayName.equals(newDisplayName)) {
                    changes.add(new RecordableChange("display name", oldDisplayName, newDisplayName));
                }
            }

            if (actions.contains(PlayerListS2CPacket.Action.UPDATE_GAME_MODE)) {
                final GameMode newGameMode = Objects.requireNonNull(entry.gameMode(), "Gamemode was null in gamemode update");
                final GameMode oldGameMode = stub.setGameMode(newGameMode);

                if (!(oldGameMode != null && oldGameMode == newGameMode)) {
                    changes.add(new RecordableChange("game mode",
                            oldGameMode == null ? Component.text("none") : oldGameMode.getSimpleTranslatableName().asComponent(),
                            newGameMode.getSimpleTranslatableName().asComponent())
                    );
                }
            }

            if (changes.isEmpty()) {
                continue;
            }

            final String username = stub.getProfile().getName();
            changes.stream().map(c -> c.toComponent(username)).forEach(messages::add);
        }

        if (messages.isEmpty()) {
            return;
        }

        this.deviousMod.sendMultipleMessages(messages);
    }

    private void reset(final boolean entries) {
        if (entries) {
            this.entryStubs.clear();
        }

        this.worldTickCount = 0;
        this.notifiedAboutLag = false;
        this.lastTimePacket = System.currentTimeMillis();
    }

    @Subscribe
    public void onConnectionEnd(final ConnectionEndEvent event) {
        this.reset(true);
    }

    @Subscribe
    public void onConnectionStart(final ConnectionStartEvent event) {
        this.reset(false);
    }

    @Subscribe
    public void onTick(final WorldTickEndEvent event) {
        this.worldTickCount++;
        final boolean tenSecondMark = this.worldTickCount % TEN_SECOND_MARK_TICKS == 0;

        if (!tenSecondMark) {
            return;
        }

        final long now = System.currentTimeMillis();
        final boolean lagging = (now - this.lastTimePacket) >= 5_000;

        if (!lagging && this.notifiedAboutLag) {
            this.sendMessage(Component.text("Server is no longer lagging!", NamedTextColor.GREEN));
            this.notifiedAboutLag = false;
            return;
        }

        if (lagging && !notifiedAboutLag) {
            this.sendMessage(Component.text("Server is now lagging!", NamedTextColor.RED));
            this.notifiedAboutLag = true;
        }
    }

    @Override
    public void onEnable() {
        final ClientPlayNetworkHandler networkHandler = this.client.getNetworkHandler();

        if (networkHandler == null) {
            DeviousMod.LOGGER.warn("Initialized Monitor module outside of game");
            return;
        }

        networkHandler.getPlayerList().stream().map(ListEntryStub::from).forEach(this.entryStubs::add);
    }

    @Override
    public void onDisable() {
        this.entryStubs.clear();
    }

    private record RecordableChange(String what, Component from, Component to) {
        Component toComponent(final String username) {
            return Component.text(username)
                    .append(Component.text(" changed their "))
                    .append(Component.text(this.what))
                    .append(Component.text(" from "))
                    .append(this.from)
                    .append(Component.text(" to "))
                    .append(this.to);
        }
    }

    private static final class ListEntryStub {
        private final GameProfile profile;
        private Component displayName;
        private GameMode gameMode;

        ListEntryStub(final @Nullable Component displayName, final @Nullable GameMode gameMode, final @NotNull GameProfile profile) {
            this.displayName = Objects.requireNonNullElse(displayName, Component.text(profile.getName()));
            this.gameMode = gameMode;
            this.profile = profile;
        }

        static ListEntryStub from(final @NotNull PlayerListS2CPacket.Entry entry) {
            return new ListEntryStub(
                    TextUtil.toAdventureNullable(entry.displayName()),
                    entry.gameMode(),
                    entry.profile()
            );
        }

        static ListEntryStub from(final @NotNull PlayerListEntry entry) {
            return new ListEntryStub(
                    TextUtil.toAdventureNullable(entry.getDisplayName()),
                    entry.getGameMode(),
                    entry.getProfile()
            );
        }

        public @Nullable GameMode setGameMode(final @NotNull GameMode newGameMode) {
            final GameMode oldGameMode = this.gameMode;
            this.gameMode = newGameMode;
            return oldGameMode;
        }

        public Pair<Component, Component> setDisplayName(final @NotNull Text text) {
            final Component asComponent = text.asComponent();

            return Pair.of(setDisplayName(asComponent), asComponent);
        }

        public Component setDisplayName(final @NotNull Component newDisplayName) {
            final Component oldDisplayName = this.displayName;
            this.displayName = newDisplayName;
            return oldDisplayName;
        }

        public GameProfile getProfile() {
            return this.profile;
        }
    }
}
