package me.allinkdev.deviousmod.module.impl;

import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.network.packet.GenericPrePacketEvent;
import me.allinkdev.deviousmod.event.network.packet.impl.PrePacketC2SEvent;
import me.allinkdev.deviousmod.event.network.packet.impl.PrePacketS2CEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import me.allinkdev.deviousmod.module.DModuleSettings;
import net.lenni0451.lambdaevents.EventHandler;

import java.util.Arrays;

public class InterceptorModule extends DModule {
    public InterceptorModule(final DModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    public String getModuleName() {
        return "Interceptor";
    }

    @Override
    public String getDescription() {
        return "Prints the raw bytes received by/sent to the client in console output.";
    }

    @Override
    protected DModuleSettings.Builder getSettingsBuilder() {
        return super.getSettingsBuilder()
                .addField("hex", "Hexadecimal View", "Displays the bytes in hexadecimal", true)
                .addField("clientside", "Show Clientside", "Displays packets that the client sends to the server.", true)
                .addField("serverside", "Show Serverside", "Displays packets that the server sends to the client.", true);
    }

    @Override
    public String getCategory() {
        return "Network";
    }

    private void logRawPacketData(final String prefix, final boolean hexadecimal, final GenericPrePacketEvent event) {
        final byte[] signedBytes = event.getData();
        final int[] unsignedBytes = new int[signedBytes.length];
        for (int i = 0; i < signedBytes.length; i++) unsignedBytes[i] = signedBytes[i] & 0xFF;
        final String display = hexadecimal ? String.join(" ", Arrays.stream(unsignedBytes).mapToObj(b -> String.format("%02x", b).toUpperCase()).toArray(String[]::new)) : Arrays.toString(unsignedBytes);
        DeviousMod.LOGGER.info("[{}] {}", prefix, display);
    }

    @EventHandler
    public void onPrePacketReceive(final PrePacketS2CEvent event) {
        if (!this.settings.getSetting("serverside", Boolean.class).getValue()) return;
        this.logRawPacketData("S->C", this.settings.getSetting("hex", Boolean.class).getValue(), event);
    }

    @EventHandler
    public void onPrePacketSend(final PrePacketC2SEvent event) {
        if (!this.settings.getSetting("clientside", Boolean.class).getValue()) return;
        this.logRawPacketData("C->S", this.settings.getSetting("hex", Boolean.class).getValue(), event);
    }
}
