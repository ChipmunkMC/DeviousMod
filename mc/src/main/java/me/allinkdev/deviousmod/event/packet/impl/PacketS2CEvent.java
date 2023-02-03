package me.allinkdev.deviousmod.event.packet.impl;

import me.allinkdev.deviousmod.event.packet.GenericPacketEvent;
import net.minecraft.network.Packet;

public class PacketS2CEvent extends GenericPacketEvent {
    public PacketS2CEvent(final Packet<?> packet) {
        super(packet);
    }
}
