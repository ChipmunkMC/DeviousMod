package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.event.network.packet.impl.PacketS2CEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.ModuleManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.ExperienceOrbSpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldEventS2CPacket;
import net.minecraft.world.WorldEvents;

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

        if (packet instanceof ParticleS2CPacket || packet instanceof ExperienceOrbSpawnS2CPacket) {
            event.setCancelled(true);
            return;
        }

        if (packet instanceof final WorldEventS2CPacket worldEventPacket) {
            final int eventId = worldEventPacket.getEventId();

            if (eventId == WorldEvents.SPLASH_POTION_SPLASHED || eventId == WorldEvents.INSTANT_SPLASH_POTION_SPLASHED) {
                event.setCancelled(true);
            }
        }
    }
}
