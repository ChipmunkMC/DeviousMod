package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.Pair;
import me.allinkdev.deviousmod.event.network.connection.ConnectionEndEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionErrorEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionStartEvent;
import me.allinkdev.deviousmod.event.network.packet.impl.PacketS2CEvent;
import me.allinkdev.deviousmod.event.tick.world.impl.WorldTickEndEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.ModuleManager;
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

import java.util.*;

public final class MonitorModule extends DModule {
    private static final long TEN_SECOND_MARK_TICKS = TimeUtil.getInTicks(10_000);
    private final Map<UUID, Pair<Component, GameMode>> playerListInformationPairMap = new HashMap<>();
    private long worldTickCount = 0;
    private long lastTimePacket = System.currentTimeMillis();
    private boolean notifiedAboutLag = false;

    public MonitorModule(final ModuleManager moduleManager) {
        super(moduleManager);
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
            lastTimePacket = System.currentTimeMillis();
        }

        if (packet instanceof final PlayerRemoveS2CPacket playerRemovePacket) {
            for (final UUID uuid : playerRemovePacket.profileIds()) {
                playerListInformationPairMap.remove(uuid);
            }

            return;
        }

        if (!(packet instanceof final PlayerListS2CPacket playerListPacket)) {
            return;
        }

        // TODO: Optimise. I don't want to iterate through every entry twice
        if (playerListPacket.getActions()
                .stream()
                .anyMatch(a -> a.equals(PlayerListS2CPacket.Action.ADD_PLAYER) || a.equals(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME))) {
            for (final PlayerListS2CPacket.Entry entry : playerListPacket.getEntries()) {
                final UUID profileId = entry.profileId();
                final GameMode gameMode = entry.gameMode();

                if (entry.displayName() == null) {
                    continue;
                }

                final Component displayName = entry.displayName()
                        .asComponent();

                final boolean exists = playerListInformationPairMap.containsKey(profileId);
                Pair<Component, GameMode> existingPair = null;

                if (exists) {
                    existingPair = playerListInformationPairMap.get(profileId);
                }

                final Pair<Component, GameMode> pair = Pair.of(displayName, exists ? existingPair.second() : gameMode);
                playerListInformationPairMap.put(profileId, pair);
            }
        }

        if (playerListPacket.getActions()
                .stream()
                .noneMatch(a -> a.equals(PlayerListS2CPacket.Action.UPDATE_GAME_MODE))) {
            return;
        }

        final List<Component> componentList = new ArrayList<>();

        for (final PlayerListS2CPacket.Entry entry : playerListPacket.getEntries()) {
            final UUID profileId = entry.profileId();
            final boolean exists = playerListInformationPairMap.containsKey(profileId);
            Pair<Component, GameMode> existingPair = null;

            if (exists) {
                existingPair = playerListInformationPairMap.get(profileId);
            }

            final Component updateMessage;
            final GameMode newGameMode = entry.gameMode();
            final boolean equals = existingPair != null && existingPair.second().equals(newGameMode);

            if (equals) {
                continue;
            }

            if (exists) {
                updateMessage = Component.text("from")
                        .append(Component.space())
                        .append(Component.text(existingPair.second().asString()))
                        .append(Component.space())
                        .append(Component.text("to"))
                        .append(Component.space())
                        .append(Component.text(newGameMode.asString()));

                final Pair<Component, GameMode> replacementPair = Pair.of(existingPair.first(), newGameMode);
                playerListInformationPairMap.put(profileId, replacementPair);
            } else {
                updateMessage = Component.text("to")
                        .append(Component.space())
                        .append(Component.text(newGameMode.asString()));
            }

            componentList.add((exists ? existingPair.first() : Component.text(profileId.toString()))
                    .append(Component.space())
                    .append(Component.text("changed their game mode"))
                    .append(Component.space())
                    .append(updateMessage));
        }

        if (componentList.isEmpty()) {
            return;
        }

        this.sendMultipleMessages(componentList);
    }

    @Subscribe
    public void onConnectionEnd(final ConnectionEndEvent event) {
        worldTickCount = 0;
        this.playerListInformationPairMap.clear();
        notifiedAboutLag = false;
        lastTimePacket = System.currentTimeMillis();
    }

    @Subscribe
    public void onConnectionStart(final ConnectionStartEvent event) {
        worldTickCount = 0;
        lastTimePacket = System.currentTimeMillis();
        notifiedAboutLag = false;
    }

    @Subscribe
    public void onConnectionError(final ConnectionErrorEvent event) {
        //final Throwable throwable = event.getThrowable();

        //DeviousMod.LOGGER.error("Packet error: ", throwable);
    }

    @Subscribe
    public void onTick(final WorldTickEndEvent event) {
        worldTickCount++;
        final boolean tenSecondMark = worldTickCount % TEN_SECOND_MARK_TICKS == 0;

        if (!tenSecondMark) {
            return;
        }

        final long now = System.currentTimeMillis();
        final boolean lagging = (now - lastTimePacket) >= 5_000;

        if (!lagging && notifiedAboutLag) {
            this.sendMessage(Component.text("Server is no longer lagging!", NamedTextColor.GREEN));
            notifiedAboutLag = false;
            return;
        }

        if (lagging && !notifiedAboutLag) {
            this.sendMessage(Component.text("Server is now lagging!", NamedTextColor.RED));
            notifiedAboutLag = true;
        }
    }

    @Override
    public void onEnable() {
        this.playerListInformationPairMap.clear();

        final ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();

        if (networkHandler == null) {
            return;
        }

        final Collection<PlayerListEntry> entries = networkHandler.getPlayerList();

        for (final PlayerListEntry entry : entries) {
            final GameProfile profile = entry.getProfile();
            final String username = profile.getName();
            final UUID profileId = profile.getId();
            final GameMode gameMode = entry.getGameMode();
            final Text displayName = entry.getDisplayName();
            final Component name = displayName == null ? Component.text(username) : displayName.asComponent();
            final Pair<Component, GameMode> pair = Pair.of(name, gameMode);

            this.playerListInformationPairMap.put(profileId, pair);
        }
    }

    @Override
    public void onDisable() {
        this.playerListInformationPairMap.clear();
    }
}
