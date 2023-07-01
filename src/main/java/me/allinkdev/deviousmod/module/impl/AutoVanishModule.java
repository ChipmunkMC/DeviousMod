package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.chat.ChatEvent;
import me.allinkdev.deviousmod.event.time.second.ServerSecondEvent;
import me.allinkdev.deviousmod.module.CommandDependentModule;
import me.allinkdev.deviousmod.module.DModuleManager;

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

    @Subscribe
    private void onServerSecond(final ServerSecondEvent event) {
        if (this.vanishEnabled || !this.commandPresent) {
            return;
        }

        DeviousMod.getInstance().getCommandQueueManager().addCommandToFront("v on");
    }

    @Subscribe
    private void onChatEvent(final ChatEvent event) {
        if (!event.getType().equals(ChatEvent.Type.SYSTEM)) {
            return;
        }

        if (!this.commandPresent) {
            return;
        }

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
