package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.network.packet.impl.PacketS2CEvent;
import me.allinkdev.deviousmod.event.screen.impl.InitScreenEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.network.message.*;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.text.Text;

import java.time.Instant;
import java.util.BitSet;
import java.util.UUID;

public final class TestModule extends DModule {
    public TestModule(final DModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    public String getCategory() {
        return "Debugging";
    }

    @Override
    public String getModuleName() {
        return "Test";
    }

    @Override
    public String getDescription() {
        return "Test 1 2 3 4 5 6 7 8 9 10";
    }

    @Subscribe
    public void onPacketC2S(final PacketS2CEvent event) {
        if (!(event.getPacket() instanceof ChatMessageC2SPacket)) {
            return;
        }

        event.setPacket(new ChatMessageC2SPacket("Testing 123", Instant.now(), 0L, null, new LastSeenMessageList.Acknowledgment(0, BitSet.valueOf(new byte[0]))));
    }

    @Subscribe
    public void onScreenInit(final InitScreenEvent event) {
        DeviousMod.LOGGER.info("screen init {}", event.getScreen().getClass().getSimpleName());
        event.addConsumer((screen, screenAccessor) -> screenAccessor.invokeAddDrawableChild(new PressableTextWidget(0, 0, 100, 10, Text.of("GAY SEX"), (b) -> {
        }, this.client.textRenderer)));
    }

    @Subscribe
    public void onPacketS2C(final PacketS2CEvent event) {
        if (!(event.getPacket() instanceof ChatMessageS2CPacket)) {
            return;
        }

        event.setPacket(new ChatMessageS2CPacket(UUID.randomUUID(), 0, null, MessageBody.ofUnsigned("SEX!!!").toSerialized(MessageSignatureStorage.create()), Text.of("SEXU"), FilterMask.PASS_THROUGH, new MessageType.Serialized(0, Text.of("sxe"), Text.of("SAX"))));
    }
}
