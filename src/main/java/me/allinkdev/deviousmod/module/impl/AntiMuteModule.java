package me.allinkdev.deviousmod.module.impl;

import me.allinkdev.deviousmod.event.chat.ChatEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionEndEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionStartEvent;
import me.allinkdev.deviousmod.event.time.second.ClientSecondEvent;
import me.allinkdev.deviousmod.module.CommandDependentModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import net.lenni0451.lambdaevents.EventHandler;

public final class AntiMuteModule extends CommandDependentModule {
    private boolean isMuted = false;

    public AntiMuteModule(final DModuleManager moduleManager) {
        super(moduleManager, "essentials:mute");
    }

    @Override
    public String getModuleName() {
        return "AntiMute";
    }

    @Override
    public String getDescription() {
        return "Prevents you from being muted.";
    }

    @Override
    public String getCategory() {
        return "Kaboom";
    }

    @EventHandler
    public void onClientSecond(final ClientSecondEvent event) {
        if (!this.commandPresent || !this.isMuted) {
            return;
        }

        this.deviousMod.getCommandQueueManager().addCommandToFront(this.getUnmuteCommand());
    }

    private String getUnmuteCommand() {
        return this.client.player == null ? "" : "essentials:mute " + this.client.player.getUuidAsString() + " 0s";
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.isMuted = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.isMuted = false;
    }

    @EventHandler
    public void onConnectionStart(final ConnectionStartEvent event) {
        this.isMuted = false;
    }

    @EventHandler
    public void onConnectionEnd(final ConnectionEndEvent event) {
        this.isMuted = false;
    }

    @EventHandler
    public void onChat(final ChatEvent event) {
        if (!event.getType().equals(ChatEvent.Type.SYSTEM) || !this.commandPresent) {
            return;
        }

        final String content = event.getContent();

        if (!content.contains(" ")) {
            return;
        }

        final String[] args = content.split(" ");

        if (args.length < 4) {
            return;
        }

        // compleks algoriyth,
        if (args[0].equals("You")) {
            if (args.length == 5 && args[1].equals("can") && args[2].equals("now") && args[3].equals("talk") && args[4].equals("again.")) {
                this.isMuted = false;
            } else if (args[1].equals("have") && args[2].equals("been")) {
                switch (args[3]) {
                    case "muted!" -> this.isMuted = true;
                    case "muted" -> {
                        if (args.length >= 6 && args[4].equals("for")) {
                            this.isMuted = !args[5].equals("now.");
                        } else {
                            this.isMuted = true;
                        }
                    }
                    case "unmuted" -> this.isMuted = false;
                    default -> {
                        // Do nothing
                    }
                }
            }
        } else if (args.length == 5 && args[0].equals("Your") && args[1].equals("voice") && args[2].equals("has") && args[3].equals("been") && args[4].equals("silenced!")) {
            this.isMuted = true;
        }

        if (!this.isMuted) {
            this.deviousMod.getCommandQueueManager().purgeInstancesOf(this.getUnmuteCommand());
        }
    }
}
