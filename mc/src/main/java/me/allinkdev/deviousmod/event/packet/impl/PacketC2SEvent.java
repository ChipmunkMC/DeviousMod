package me.allinkdev.deviousmod.event.packet.impl;

import me.allinkdev.deviousmod.event.packet.GenericPacketEvent;
import net.minecraft.network.Packet;

public class PacketC2SEvent extends GenericPacketEvent {
    public PacketC2SEvent(final Packet<?> packet) {
        super(packet);
    }
}
