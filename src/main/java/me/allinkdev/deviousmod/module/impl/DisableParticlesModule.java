package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.event.network.packet.impl.PacketS2CEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.ModuleManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;

public final class DisableParticlesModule extends DModule {
    public DisableParticlesModule(final ModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    public String getCategory() {
        return "Render";
    }

    @Override
    public String getModuleName() {
        return "DisableParticles";
    }

    @Override
    public String getDescription() {
        return "Cancels particle packets.";
    }

    @Subscribe
    public void onPacketS2CEvent(final PacketS2CEvent event) {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof ParticleS2CPacket) {
            event.setCancelled(true);
        }
    }
}
