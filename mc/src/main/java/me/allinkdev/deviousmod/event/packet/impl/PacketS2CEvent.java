package me.allinkdev.deviousmod.event.packet.impl;

import lombok.Getter;
import lombok.Setter;
import me.allinkdev.deviousmod.event.packet.GenericPacketEvent;
import net.minecraft.network.Packet;

@Getter
@Setter
public class PacketS2CEvent extends GenericPacketEvent {
    /**
     * Should we disconnect from the server on an invalid packet, class cast exception (could be due to modification of packet classes on the server) or a RejectedExecutionException (server has shutdown)
     */
    private boolean disconnectingOnExceptions = true;

    public PacketS2CEvent(final Packet<?> packet) {
        super(packet);
    }
}
