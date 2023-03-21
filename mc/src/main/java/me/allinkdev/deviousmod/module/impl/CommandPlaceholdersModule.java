package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import com.mojang.brigadier.suggestion.Suggestion;
import me.allinkdev.deviousmod.command.CommandCompletionManager;
import me.allinkdev.deviousmod.event.self.chat.impl.SelfSendCommandEvent;
import me.allinkdev.deviousmod.event.tick.impl.ClientTickEndEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.ModuleManager;
import me.allinkdev.deviousmod.util.TimeUtil;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import java.util.Arrays;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public final class CommandPlaceholdersModule extends DModule {
    private static final long BASE_DELAY = 70L;
    private final Queue<String> commandQueue = new LinkedBlockingQueue<>();
    private long tickCount = 0;

    public CommandPlaceholdersModule(final ModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    public String getModuleName() {
        return "CommandPlaceholders";
    }

    @Override
    public String getDescription() {
        return "Allows you to use all_suggestions to run a command with every completion.";
    }

    @Subscribe
    public void onSendCommandEvent(final SelfSendCommandEvent event) {
        final String command = event.getMessage();

        if (!command.contains("all_suggestions")) {
            return;
        }

        event.setCancelled(true);
        final String trimmed = command.trim();
        final String[] parts = trimmed.split(" ");
        int partIndex = -1;

        for (int i = 0; i < parts.length; i++) {
            final String element = parts[i];

            if (element.equals("all_suggestions")) {
                partIndex = i;
                break;
            }
        }

        if (partIndex == -1) {
            // Probably log something here in the future
            return;
        }

        final String[] partialCommandParts = Arrays.copyOfRange(parts, 0, partIndex);
        final String[] commandRemainderParts = Arrays.copyOfRange(parts, partIndex + 1, parts.length);
        final String partialCommand = String.join(" ", partialCommandParts) + " ";
        final String commandRemainder = String.join(" ", commandRemainderParts);
        logger.info(partialCommand);

        CommandCompletionManager.getCompletion(partialCommand, (suggestions -> {
            final Set<String> completions = suggestions.getList()
                    .stream()
                    .map(Suggestion::getText)
                    .collect(Collectors.toUnmodifiableSet());

            for (final String completion : completions) {
                final String completedCommand = partialCommand + completion + " " + commandRemainder;
                this.queueCommand(completedCommand);
            }
        }));
    }

    private void queueCommand(final String command) {
        final ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();

        if (networkHandler == null) {
            return;
        }

        commandQueue.add(command);
    }

    @Subscribe
    public void onTick(final ClientTickEndEvent event) {
        tickCount++;


        final ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();

        if (networkHandler == null) {
            return;
        }

        final long commandDelay = TimeUtil.calculateCommandDelay(networkHandler);
        final long commandDelayInTicks = TimeUtil.getInTicks(commandDelay);

        if (tickCount % commandDelayInTicks != 0) {
            return;
        }


        final String command = this.commandQueue.poll();

        if (command == null) {
            return;
        }

        networkHandler.sendChatCommand(command);
    }

}
