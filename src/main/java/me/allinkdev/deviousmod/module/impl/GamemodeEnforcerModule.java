package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.network.packet.impl.PacketS2CEvent;
import me.allinkdev.deviousmod.event.self.chat.impl.SelfSendCommandEvent;
import me.allinkdev.deviousmod.event.time.second.ClientSecondEvent;
import me.allinkdev.deviousmod.module.CommandDependentModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import me.allinkdev.deviousmod.util.BukkitUtil;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.world.GameMode;

public final class GamemodeEnforcerModule extends CommandDependentModule {
    private static final String MINECRAFT_PREFIX = "minecraft";
    private static final String ESSENTIALS_PREFIX = "essentials";
    private GameMode currentGameMode = GameMode.SURVIVAL;
    private GameMode requiredGameMode = GameMode.CREATIVE; // TODO: Make this configurable in module settings

    public GamemodeEnforcerModule(final DModuleManager moduleManager) {
        super(moduleManager, "minecraft:gamemode");
    }

    @Override
    public String getModuleName() {
        return "GamemodeEnforcer";
    }

    @Override
    public String getDescription() {
        return "Enforces the player's requested gamemode.";
    }

    @Override
    public String getCategory() {
        return "Kaboom";
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (this.client.interactionManager == null) {
            return;
        }

        this.currentGameMode = this.client.interactionManager.getCurrentGameMode();
    }

    @Subscribe
    private void onPacketReceive(final PacketS2CEvent event) {
        final Packet<?> packet = event.getPacket();
        if (packet instanceof final GameJoinS2CPacket gameJoinPacket) {
            this.currentGameMode = gameJoinPacket.gameMode();
        } else if (packet instanceof final GameStateChangeS2CPacket gameStateChangePacket) {
            final ClientPlayerEntity player = this.client.player;

            if (player == null) {
                return;
            }

            if (gameStateChangePacket.getReason() != GameStateChangeS2CPacket.GAME_MODE_CHANGED) {
                return;
            }

            switch ((int) Math.floor(gameStateChangePacket.getValue())) {
                case 0 -> this.currentGameMode = GameMode.SURVIVAL;
                case 1 -> this.currentGameMode = GameMode.CREATIVE;
                case 2 -> this.currentGameMode = GameMode.ADVENTURE;
                case 3 -> this.currentGameMode = GameMode.SPECTATOR;
                default -> {
                    // Do nothing
                }
            }

            if (this.currentGameMode.equals(this.requiredGameMode)) {
                DeviousMod.getInstance().getCommandQueueManager().purgeInstancesOf(this.getGamemodeChangeCommand());
            }
        }
    }

    @Subscribe
    private void onClientSecond(final ClientSecondEvent event) {
        if (this.currentGameMode.equals(this.requiredGameMode) || !this.commandPresent) {
            return;
        }

        DeviousMod.getInstance().getCommandQueueManager().addCommandToFront(this.getGamemodeChangeCommand());
    }

    private String getGamemodeChangeCommand() {
        return "minecraft:gamemode " + this.requiredGameMode.getName();
    }

    @Subscribe
    private void onSelfSendCommand(final SelfSendCommandEvent event) {
        if (event.wasQueued()) {
            return;
        }

        final String command = event.getMessage();

        if (BukkitUtil.isSameCommand(command, MINECRAFT_PREFIX, "gamemode survival") || BukkitUtil.isSameCommand(command, ESSENTIALS_PREFIX, "gms", "egms")) {
            this.requiredGameMode = GameMode.SURVIVAL;
        } else if (BukkitUtil.isSameCommand(command, MINECRAFT_PREFIX, "gamemode adventure") || BukkitUtil.isSameCommand(command, ESSENTIALS_PREFIX, "gma", "egma")) {
            this.requiredGameMode = GameMode.ADVENTURE;
        } else if (BukkitUtil.isSameCommand(command, MINECRAFT_PREFIX, "gamemode spectator") || BukkitUtil.isSameCommand(command, ESSENTIALS_PREFIX, "gmsp", "egmsp")) {
            this.requiredGameMode = GameMode.SPECTATOR;
        } else if (BukkitUtil.isSameCommand(command, MINECRAFT_PREFIX, "gamemode creative") || BukkitUtil.isSameCommand(command, ESSENTIALS_PREFIX, "gmc", "egmc")) {
            this.requiredGameMode = GameMode.CREATIVE;
        }
    }
}
