package me.allinkdev.deviousmod.event.packet.impl;

import lombok.Getter;
import lombok.Setter;
import me.allinkdev.deviousmod.event.packet.GenericPacketEvent;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketCallbacks;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
public class PacketC2SEvent extends GenericPacketEvent {
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
}
