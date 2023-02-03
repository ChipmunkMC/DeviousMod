package me.allinkdev.deviousmod.event.packet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.allinkdev.deviousmod.event.Cancellable;
import net.minecraft.network.Packet;

@Getter
@RequiredArgsConstructor
public class GenericPacketEvent extends Cancellable {
    private final Packet<?> packet;
}
