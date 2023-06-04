package me.allinkdev.deviousmod.event.network.packet;

import com.github.allinkdev.deviousmod.api.event.Cancellable;
import net.minecraft.network.packet.Packet;

public class GenericPacketEvent extends Cancellable {
    protected Packet<?> packet;

    protected GenericPacketEvent(final Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }

    public void setPacket(final Packet<?> newPacket) {
        this.packet = newPacket;
    }
}
