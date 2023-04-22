package me.allinkdev.deviousmod.event.network.packet.impl;

import me.allinkdev.deviousmod.event.network.packet.GenericPacketEvent;
import net.minecraft.network.packet.Packet;

public final class PacketS2CEvent extends GenericPacketEvent {
    /**
     * Should we disconnect from the server on an invalid packet, class cast exception (could be due to modification of packet classes on the server) or a RejectedExecutionException (server has shutdown)
     */
    private boolean disconnectingOnExceptions = true;

    public PacketS2CEvent(final Packet<?> packet) {
        super(packet);
    }

    public boolean isDisconnectingOnExceptions() {
        return this.disconnectingOnExceptions;
    }

    public void setDisconnectingOnExceptions(final boolean value) {
        this.disconnectingOnExceptions = value;
    }
}
