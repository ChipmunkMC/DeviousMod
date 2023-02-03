package me.allinkdev.deviousmod.event.packet.impl;

import me.allinkdev.deviousmod.event.packet.GenericPacketEvent;
import net.minecraft.network.Packet;

public class PacketC2SEvent extends GenericPacketEvent {
    public PacketC2SEvent(final Packet<?> packet) {
        super(packet);
    }

    public static boolean packetC2S(final Packet<?> packet) {
        final PacketC2SEvent event = new PacketC2SEvent(packet);

        eventBus.post(event);

        return event.isCancelled();
    }
}
