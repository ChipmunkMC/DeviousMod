package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.event.chat.ChatEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionEndEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionStartEvent;
import me.allinkdev.deviousmod.event.time.second.ServerSecondEvent;
import me.allinkdev.deviousmod.event.window.WindowFocusChangeEvent;
import me.allinkdev.deviousmod.module.CommandDependentModule;
import me.allinkdev.deviousmod.module.DModuleManager;

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

    @Subscribe
    private void onConnectionStart(final ConnectionStartEvent event) {
        this.reset();
    }

    @Subscribe
    private void onConnectionEnd(final ConnectionEndEvent event) {
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

    @Subscribe
    private void onServerSecond(final ServerSecondEvent event) {
        if (!this.commandPresent || this.afkStatus == this.targetAfk) {
            return;
        }

        this.deviousMod.getCommandQueueManager().addCommandToFront("eafk");
    }

    @Subscribe
    private void onFocusChanged(final WindowFocusChangeEvent event) {
        this.targetAfk = !event.getNewFocus();
    }

    @Subscribe
    private void onChat(final ChatEvent event) {
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
