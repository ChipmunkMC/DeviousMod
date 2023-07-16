package me.allinkdev.deviousmod.module.impl;

import com.github.allinkdev.deviousmod.api.experiments.Experimental;
import me.allinkdev.deviousmod.event.network.connection.ConnectionEndEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionStartEvent;
import me.allinkdev.deviousmod.event.network.packet.impl.PacketS2CEvent;
import me.allinkdev.deviousmod.event.time.second.ServerSecondEvent;
import me.allinkdev.deviousmod.module.CommandDependentModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import net.lenni0451.lambdaevents.EventHandler;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

@Experimental(hide = false)
public final class MovementEnforcerModule extends CommandDependentModule {
    private double targetX;
    private double targetY;
    private double targetZ;
    private float targetYaw;
    private float targetPitch;
    private boolean enforce;

    public MovementEnforcerModule(final DModuleManager moduleManager) {
        super(moduleManager, "etppos");
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

    @EventHandler
    public void onConnectionEnd(final ConnectionEndEvent event) {
        this.reset();
    }

    @EventHandler
    public void onConnectionStart(final ConnectionStartEvent event) {
        this.reset();
    }

    @EventHandler
    public void onServerSecond(final ServerSecondEvent event) {
        if (!this.enforce || !this.commandPresent) return;
        final String commandBuilder = "etppos " +
                this.targetX +
                " " +
                this.targetY +
                " " +
                this.targetZ +
                " " +
                this.targetYaw +
                " " +
                this.targetPitch;

        this.deviousMod.getCommandQueueManager().addCommandToFront(commandBuilder);
    }

    private void purge() {
        this.deviousMod.getCommandQueueManager().purgeIf(c -> c.startsWith("essentials:tppos"));
    }

    @EventHandler
    public void onPacketReceive(final PacketS2CEvent event) {
        // TODO: Death stuff

        if (!this.commandPresent || this.client.player == null || !(event.getPacket() instanceof final PlayerPositionLookS2CPacket positionPacket))
            return;
        final double serverX = positionPacket.getX();
        final double serverY = positionPacket.getY();
        final double serverZ = positionPacket.getZ();
        final double serverYaw = positionPacket.getYaw();
        final double serverPitch = positionPacket.getPitch();

        final double clientX = this.client.player.getX();
        final double clientY = this.client.player.getY();
        final double clientZ = this.client.player.getZ();
        final float clientYaw = this.client.player.getYaw();
        final float clientPitch = this.client.player.getPitch();

        final double diffX = Math.abs(serverX - clientX);
        final double diffY = Math.abs(serverY - clientY);
        final double diffZ = Math.abs(serverZ - clientZ);
        final float diffYaw = (float) Math.abs(serverYaw - clientYaw);
        final float diffPitch = (float) Math.abs(serverPitch - clientPitch);

        // TODO: Configurable threshold
        if (diffX > 3 || diffY > 3 || diffZ > 3 || diffYaw > 3 || diffPitch > 3) {
            this.targetX = clientX;
            this.targetY = clientY;
            this.targetZ = clientZ;
            this.targetYaw = clientYaw;
            this.targetPitch = clientPitch;
            this.enforce = true;
        } else if (diffX < 3 && diffY < 3 && diffZ < 3 && diffYaw < 3 && diffPitch < 3) {
            this.enforce = false;
            this.purge();
        }
    }
}
