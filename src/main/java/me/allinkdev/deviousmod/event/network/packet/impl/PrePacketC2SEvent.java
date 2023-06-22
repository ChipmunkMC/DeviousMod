package me.allinkdev.deviousmod.event.network.packet.impl;

import me.allinkdev.deviousmod.event.network.packet.GenericPrePacketEvent;

public final class PrePacketC2SEvent extends GenericPrePacketEvent {
    public PrePacketC2SEvent(final byte[] data) {
        super(data);
    }
}
