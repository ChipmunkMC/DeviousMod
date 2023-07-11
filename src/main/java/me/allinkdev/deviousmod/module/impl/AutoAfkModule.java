package me.allinkdev.deviousmod.module.impl;

import me.allinkdev.deviousmod.event.chat.ChatEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionEndEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionStartEvent;
import me.allinkdev.deviousmod.event.time.second.ClientSecondEvent;
import me.allinkdev.deviousmod.event.window.WindowFocusChangeEvent;
import me.allinkdev.deviousmod.module.CommandDependentModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import net.lenni0451.lambdaevents.EventHandler;

public class AutoAfkModule extends CommandDependentModule {
    private boolean afkStatus;
    private boolean targetAfk;

    public AutoAfkModule(final DModuleManager moduleManager) {
        super(moduleManager, "essentials:afk");
    }

    @Override
    public String getModuleName() {
        return "AutoAfk";
    }

    @Override
    public String getDescription() {
        return "Automatically un/marks you as AFK when you un/focus the window.";
    }

    @Override
    public String getCategory() {
        return "Miscellaneous";
    }

    private void reset() {
        this.targetAfk = false;
        this.afkStatus = false;
    }

    @EventHandler
    public void onConnectionStart(final ConnectionStartEvent event) {
        this.reset();
    }

    @EventHandler
    public void onConnectionEnd(final ConnectionEndEvent event) {
        this.reset();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.reset();
    }

    @EventHandler
    public void onClientSecond(final ClientSecondEvent event) {
        if (!this.commandPresent || this.afkStatus == this.targetAfk) {
            return;
        }

        this.deviousMod.getCommandQueueManager().addCommandToFront("eafk");
    }

    @EventHandler
    public void onFocusChanged(final WindowFocusChangeEvent event) {
        this.targetAfk = !event.getNewFocus();
    }

    @EventHandler
    public void onChat(final ChatEvent event) {
        if (!event.getType().equals(ChatEvent.Type.SYSTEM) || !this.commandPresent) {
            return;
        }

        final String content = event.getContent();
        if (content.equals("You are now AFK.")) {
            this.afkStatus = true;
        } else if (content.equals("You are no longer AFK.")) {
            this.afkStatus = false;
        }

        if (this.afkStatus == this.targetAfk) {
            this.deviousMod.getCommandQueueManager().purgeInstancesOf("eafk");
        }
    }
}
