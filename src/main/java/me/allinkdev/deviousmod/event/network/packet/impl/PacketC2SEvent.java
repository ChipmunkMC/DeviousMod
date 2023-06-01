package me.allinkdev.deviousmod.event.network.packet.impl;

import me.allinkdev.deviousmod.event.network.packet.GenericPacketEvent;
import net.minecraft.network.packet.Packet;

public final class PacketC2SEvent extends GenericPacketEvent {
    public PacketC2SEvent(final Packet<?> packet) {
        super(packet);
    }
}
