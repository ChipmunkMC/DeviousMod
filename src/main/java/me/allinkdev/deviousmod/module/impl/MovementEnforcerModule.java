package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.event.network.connection.ConnectionEndEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionStartEvent;
import me.allinkdev.deviousmod.event.network.packet.impl.PacketS2CEvent;
import me.allinkdev.deviousmod.event.time.second.ServerSecondEvent;
import me.allinkdev.deviousmod.module.CommandDependentModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public final class MovementEnforcerModule extends CommandDependentModule {
    private double targetX;
    private double targetY;
    private double targetZ;
    private boolean enforce;

    public MovementEnforcerModule(final DModuleManager moduleManager) {
        super(moduleManager, "essentials:tppos");
    }

    @Override
    public String getModuleName() {
        return "MovementEnforcer";
    }

    @Override
    public String getDescription() {
        return "Prevents you from being teleported.";
    }

    @Override
    public String getCategory() {
        return "Kaboom";
    }

    private void reset() {
        this.targetX = 0;
        this.targetY = 0;
        this.targetZ = 0;
        this.enforce = false;
    }

    @Subscribe
    private void onConnectionEnd(final ConnectionEndEvent event) {
        this.reset();
    }

    @Subscribe
    private void onConnectionStart(final ConnectionStartEvent event) {
        this.reset();
    }

    @Subscribe
    private void onServerSecond(final ServerSecondEvent event) {
        if (!this.enforce || !this.commandPresent) {
            return;
        }

        this.deviousMod.getCommandQueueManager().addCommandToFront("essentials:tppos " + this.targetX + " " + this.targetY + " " + this.targetZ);
    }

    private void purge() {
        this.deviousMod.getCommandQueueManager().purgeIf(c -> c.startsWith("essentials:tppos"));
    }

    @Subscribe
    private void onPacketReceive(final PacketS2CEvent event) {
        // TODO: Death stuff

        if (!this.commandPresent) {
            return;
        }

        if (!(event.getPacket() instanceof final PlayerPositionLookS2CPacket positionPacket)) {
            return;
        }

        if (this.client.player == null) {
            return;
        }

        final double serverX = positionPacket.getX();
        final double serverY = positionPacket.getY();
        final double serverZ = positionPacket.getZ();

        final double clientX = this.client.player.getX();
        final double clientY = this.client.player.getY();
        final double clientZ = this.client.player.getZ();

        final double diffX = Math.abs(serverX - clientX);
        final double diffY = Math.abs(serverY - clientY);
        final double diffZ = Math.abs(serverZ - clientZ);

        // TODO: Configurable threshold
        if (diffX > 3 || diffY > 3 || diffZ > 3) {
            this.targetX = clientX;
            this.targetY = clientY;
            this.targetZ = clientZ;
            this.enforce = true;
        } else if (diffX < 3 && diffY < 3 && diffZ < 3) {
            this.enforce = false;
            this.purge();
        }
    }
}
