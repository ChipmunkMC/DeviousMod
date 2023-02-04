package me.allinkdev.deviousmod.event.packet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.allinkdev.deviousmod.event.Cancellable;
import net.minecraft.network.Packet;

@Getter
@Setter
@AllArgsConstructor
public class GenericPacketEvent extends Cancellable {
    private Packet<?> packet;
}
