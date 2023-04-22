package me.allinkdev.deviousmod.event.network.packet.impl;

import me.allinkdev.deviousmod.event.network.packet.GenericPacketEvent;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import org.jetbrains.annotations.Nullable;

public final class PacketC2SEvent extends GenericPacketEvent {
    /**
     * Should we add a listener that fires exceptions on failures?
     * Defaults to true.
     */
    private boolean throwingExceptions = true;
    @Nullable
    private PacketCallbacks packetCallbacks;

    public PacketC2SEvent(final Packet<?> packet, @Nullable final PacketCallbacks packetCallbacks) {
        super(packet);
        this.packetCallbacks = packetCallbacks;
    }

    public PacketCallbacks getPacketCallbacks() {
        return this.packetCallbacks;
    }

    public boolean isThrowingExceptions() {
        return this.throwingExceptions;
    }
}
