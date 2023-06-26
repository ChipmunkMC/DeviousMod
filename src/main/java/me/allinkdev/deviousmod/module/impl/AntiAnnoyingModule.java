package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.event.effect.ShowStatusEffectIconEvent;
import me.allinkdev.deviousmod.event.screen.impl.SetScreenEvent;
import me.allinkdev.deviousmod.event.self.SelfReducedDebugInfoEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import me.allinkdev.deviousmod.module.DModuleSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.gui.screen.DemoScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;

public final class AntiAnnoyingModule extends DModule {

    public AntiAnnoyingModule(final DModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    protected DModuleSettings.Builder getSettingsBuilder() {
        return super.getSettingsBuilder().
                addField("demo", "Ignore demo screen", "Prevents the server from opening the demo screen.", true).
                addField("end", "Ignore end screen", "Prevents the server from opening the end screen.", true).

                addField("rdi", "Ignored Reduced Debug Info", "Prevents the server from force enabling the reduced debug info setting.", true).
                addField("status_effect", "Ignore status effect state", "Prevents the server from disabling the status effect hud.", true);
    }

    @Subscribe
    public void onSetScreen(final SetScreenEvent e) {
        if (e.getTarget() instanceof DemoScreen && this.settings.getSetting("demo", Boolean.class).getValue()) {
            e.setTarget(null);
        }
        if (e.getTarget() instanceof CreditsScreen && this.settings.getSetting("end", Boolean.class).getValue()) {
            e.setTarget(null);
            final ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return;
            player.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
        }
    }

    @Subscribe
    public void onSelfReducedDebugInfo(final SelfReducedDebugInfoEvent e) {
        e.setReducedDebugInfo(this.settings.getSetting("rdi", Boolean.class).getValue());
    }

    @Subscribe
    public void onShowStatusEffectIcon(final ShowStatusEffectIconEvent e) {
        e.setShowIcon(this.settings.getSetting("status_effect", Boolean.class).getValue());
    }

    @Override
    public String getModuleName() {
        return "AntiAnnoying";
    }

    @Override
    public String getDescription() {
        return "Prevents the server from annoying you with certain packets";
    }

    @Override
    public String getCategory() {
        return "Comfort";
    }
}
