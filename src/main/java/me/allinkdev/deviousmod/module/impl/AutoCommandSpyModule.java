package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.chat.ChatEvent;
import me.allinkdev.deviousmod.event.time.second.ServerSecondEvent;
import me.allinkdev.deviousmod.module.CommandDependentModule;
import me.allinkdev.deviousmod.module.DModuleManager;
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

    @Subscribe
    private void onServerSecond(final ServerSecondEvent event) {
        if (this.commandSpyEnabled || !this.commandPresent) {
            return;
        }

        DeviousMod.getInstance().getCommandQueueManager().addCommandToFront("c on");
    }

    @Subscribe
    private void onChatEvent(final ChatEvent event) {
        if (!event.getType().equals(ChatEvent.Type.SYSTEM) || !this.commandPresent) {
            return;
        }

        final ClientPlayNetworkHandler networkHandler = DeviousMod.CLIENT.getNetworkHandler();

        if (networkHandler == null) {
            return;
        }

        final String content = event.getContent();
        if (content.equals("Successfully enabled CommandSpy")) {
            this.commandSpyEnabled = true;
            DeviousMod.getInstance().getCommandQueueManager().purgeInstancesOf("c on");
        } else if (content.equals("Successfully disabled CommandSpy")) {
            this.commandSpyEnabled = false;
        }
    }
}
