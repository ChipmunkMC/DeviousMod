package me.allinkdev.deviousmod.module.impl;

import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.chat.ChatEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionEndEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionStartEvent;
import me.allinkdev.deviousmod.event.time.second.ClientSecondEvent;
import me.allinkdev.deviousmod.module.CommandDependentModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import net.lenni0451.lambdaevents.EventHandler;

public final class AutoVanishModule extends CommandDependentModule {
    private boolean vanishEnabled;

    public AutoVanishModule(final DModuleManager moduleManager) {
        super(moduleManager, "essentials:vanish");
    }

    @Override
    public String getModuleName() {
        return "AutoVanish";
    }

    @Override
    public String getDescription() {
        return "Automatically maintains vanish status.";
    }

    @Override
    public String getCategory() {
        return "Kaboom";
    }

    @EventHandler
    public void onClientSecond(final ClientSecondEvent event) {
        if (this.vanishEnabled || !this.commandPresent) return;
        DeviousMod.getInstance().getCommandQueueManager().addCommandToFront("v on");
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.vanishEnabled = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.vanishEnabled = false;
    }

    @EventHandler
    public void onConnectionStart(final ConnectionStartEvent event) {
        this.vanishEnabled = false;
    }

    @EventHandler
    public void onConnectionEnd(final ConnectionEndEvent event) {
        this.vanishEnabled = false;
    }

    @EventHandler
    public void onChatEvent(final ChatEvent event) {
        if (!event.getType().equals(ChatEvent.Type.SYSTEM) || !this.commandPresent) return;
        final String content = event.getContent();
        if (content.startsWith("Vanish for")) {
            if (content.endsWith("disabled")) {
                this.vanishEnabled = false;
            } else if (content.endsWith("enabled")) {
                this.vanishEnabled = true;
                DeviousMod.getInstance().getCommandQueueManager().purgeInstancesOf("v on");
            }
        }
    }
}
