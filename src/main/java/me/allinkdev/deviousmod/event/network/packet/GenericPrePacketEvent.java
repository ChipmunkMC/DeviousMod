package me.allinkdev.deviousmod.event.network.packet;

import com.github.allinkdev.deviousmod.api.event.Event;

import java.util.Arrays;

public class GenericPrePacketEvent implements Event {
    private final byte[] data;

    public GenericPrePacketEvent(final byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return Arrays.copyOf(this.data, this.data.length);
    }
}
