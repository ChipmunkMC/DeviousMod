package me.allinkdev.deviousmod.module.impl;

import me.allinkdev.deviousmod.event.network.packet.impl.PacketS2CEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import net.lenni0451.lambdaevents.EventHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket;

public final class DiscardScreenClosesModule extends DModule {
    public DiscardScreenClosesModule(final DModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    public String getModuleName() {
        return "DiscardScreenCloses";
    }

    @Override
    public String getDescription() {
        return "Discards screen close packets sent by the server.";
    }

    @Override
    public String getCategory() {
        return "Network";
    }

    @EventHandler
    public void onPacketRecieve(final PacketS2CEvent event) {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof CloseScreenS2CPacket || packet instanceof CloseHandledScreenC2SPacket) {
            event.setCancelled(true);
        }
    }
}
