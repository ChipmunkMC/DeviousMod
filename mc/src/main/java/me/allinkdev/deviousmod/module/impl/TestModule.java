package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.event.packet.impl.PacketS2CEvent;
import me.allinkdev.deviousmod.module.DModule;

public class TestModule extends DModule {
    @Override
    public String getModuleName() {
        return "Test";
    }

    @Override
    public String getDescription() {
        return "Test 1 2 3 4 5 6 7 8 9 10";
    }

    @Subscribe
    public void onPacketS2C(final PacketS2CEvent event) {
        logger.info("s2c: {}", event.getPacket().getClass().getTypeName());
    }

    @Subscribe
    public void onPacketC2S(final PacketS2CEvent event) {
        logger.info("c2s: {}", event.getPacket().getClass().getTypeName());
    }
}
