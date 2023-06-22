package me.allinkdev.deviousmod.event.network.packet.impl;

import me.allinkdev.deviousmod.event.network.packet.GenericPrePacketEvent;

public final class PrePacketS2CEvent extends GenericPrePacketEvent {
    public PrePacketS2CEvent(final byte[] data) {
        super(data);
    }
}
