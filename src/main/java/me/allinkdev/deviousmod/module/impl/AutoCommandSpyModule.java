package me.allinkdev.deviousmod.module.impl;

import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.chat.ChatEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionEndEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionStartEvent;
import me.allinkdev.deviousmod.event.time.second.ClientSecondEvent;
import me.allinkdev.deviousmod.module.CommandDependentModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import net.lenni0451.lambdaevents.EventHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;

public final class AutoCommandSpyModule extends CommandDependentModule {
    private boolean commandSpyEnabled;

    public AutoCommandSpyModule(final DModuleManager moduleManager) {
        super(moduleManager, "commandspy:commandspy");
    }

    @Override
    public String getModuleName() {
        return "AutoCommandSpy";
    }

    @Override
    public String getDescription() {
        return "Automatically re/enables CommandSpy.";
    }

    @Override
    public String getCategory() {
        return "Kaboom";
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.commandSpyEnabled = false;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.commandSpyEnabled = false;
    }

    @EventHandler
    public void onConnectionStart(final ConnectionStartEvent event) {
        this.commandSpyEnabled = false;
    }

    @EventHandler
    public void onConnectionEnd(final ConnectionEndEvent event) {
        this.commandSpyEnabled = false;
    }

    @EventHandler
    public void onClientSecond(final ClientSecondEvent event) {
        if (this.commandSpyEnabled || !this.commandPresent) return;
        DeviousMod.getInstance().getCommandQueueManager().addCommandToFront("c on");
    }

    @EventHandler
    public void onChatEvent(final ChatEvent event) {
        if (!event.getType().equals(ChatEvent.Type.SYSTEM) || !this.commandPresent) return;
        final ClientPlayNetworkHandler networkHandler = DeviousMod.CLIENT.getNetworkHandler();

        if (networkHandler == null) return;
        final String content = event.getContent();
        if (content.equals("Successfully enabled CommandSpy")) {
            this.commandSpyEnabled = true;
            DeviousMod.getInstance().getCommandQueueManager().purgeInstancesOf("c on");
        } else if (content.equals("Successfully disabled CommandSpy")) {
            this.commandSpyEnabled = false;
        }
    }
}
