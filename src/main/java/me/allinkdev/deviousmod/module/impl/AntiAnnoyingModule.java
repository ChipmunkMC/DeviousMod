package me.allinkdev.deviousmod.module.impl;

import me.allinkdev.deviousmod.event.effect.ShowStatusEffectIconEvent;
import me.allinkdev.deviousmod.event.network.packet.impl.PacketS2CEvent;
import me.allinkdev.deviousmod.event.screen.impl.SetScreenEvent;
import me.allinkdev.deviousmod.event.self.SelfReducedDebugInfoEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import me.allinkdev.deviousmod.module.DModuleSettings;
import net.lenni0451.lambdaevents.EventHandler;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.DemoScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.s2c.play.OpenHorseScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenWrittenBookS2CPacket;

public final class AntiAnnoyingModule extends DModule {
    private long lastClose;
    private long lastOpen;

    private void reset() {
        this.lastClose = 0;
        this.lastOpen = 0;
    }

    @Override
    public void onEnable() {
        this.reset();
    }

    @Override
    public void onDisable() {
        this.reset();
    }

    public AntiAnnoyingModule(final DModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    protected DModuleSettings.Builder getSettingsBuilder() {
        return super.getSettingsBuilder()
                .addField("demo", "No Demo Screen", "Prevents the server from opening the demo screen.", true)
                .addField("end", "No End Screen", "Prevents the server from opening the end screen.", true)
                .addField("inventory_abuse", "Prevent Inventory Abuse", "Prevents the server from abusing inventories.", true)
                .addField("rdi", "No Reduced Debug Info", "Prevents the server from force enabling the reduced debug info setting.", true)
                .addField("status_effect", "No Status Effect State", "Prevents the server from disabling the status effect hud.", true);
    }

    @EventHandler
    public void onSetScreen(final SetScreenEvent e) {
        if (e.getTarget() == null) {
            if (this.settings.getSetting("inventory_abuse", Boolean.class).getValue()) this.lastClose = System.currentTimeMillis();
            return;
        }
        if (e.getTarget() instanceof DemoScreen && this.settings.getSetting("demo", Boolean.class).getValue()) e.setTarget(null);
        if (e.getTarget() instanceof CreditsScreen && this.settings.getSetting("end", Boolean.class).getValue()) {
            e.setTarget(null);
            final ClientPlayerEntity player = this.client.player;
            if (player == null) return;
            player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
        }
    }

    @EventHandler
    public void onSelfReducedDebugInfo(final SelfReducedDebugInfoEvent e) {
        if (this.settings.getSetting("rdi", Boolean.class).getValue()) e.setReducedDebugInfo(false);
    }

    @EventHandler
    public void onShowStatusEffectIcon(final ShowStatusEffectIconEvent e) {
        if (this.settings.getSetting("status_effect", Boolean.class).getValue()) e.setShowIcon(true);
    }

    @EventHandler
    public void onPacketReceive(final PacketS2CEvent event) {
        if (!this.settings.getSetting("inventory_abuse", Boolean.class).getValue()) return;
        final Packet<?> packet = event.getPacket();
        if (!(packet instanceof OpenHorseScreenS2CPacket) && !(packet instanceof OpenWrittenBookS2CPacket) && !(packet instanceof OpenScreenS2CPacket)) return;

        this.lastOpen = System.currentTimeMillis();
        if (this.client.currentScreen != null) {
            event.setCancelled(true);
            return;
        }

        final long diff = this.lastOpen - this.lastClose;

        if (diff >= 1000) {
            return;
        }

        event.setCancelled(true);
    }

    @Override
    public String getModuleName() {
        return "AntiAnnoying";
    }

    @Override
    public String getDescription() {
        return "Prevents common annoyances.";
    }

    @Override
    public String getCategory() {
        return "Comfort";
    }
}
