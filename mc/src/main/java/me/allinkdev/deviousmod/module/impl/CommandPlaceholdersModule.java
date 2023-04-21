package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import me.allinkdev.deviousmod.command.CommandCompletionManager;
import me.allinkdev.deviousmod.event.network.connection.ConnectionEndEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionStartEvent;
import me.allinkdev.deviousmod.event.self.chat.impl.SelfSendCommandEvent;
import me.allinkdev.deviousmod.event.tick.impl.ClientTickEndEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.ModuleManager;
import me.allinkdev.deviousmod.util.TimeUtil;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public final class CommandPlaceholdersModule extends DModule {
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
                if (partIndex != -1) {
                    this.parseRecursive(parts);
                    return;
                }

                partIndex = i;
            }
        }

        if (partIndex == -1) {
            // Probably log something here in the future
            return;
        }

        final String[] partialCommandParts = Arrays.copyOfRange(parts, 0, partIndex);
        final String[] commandRemainderParts = Arrays.copyOfRange(parts, partIndex + 1, parts.length);
        final String partialCommand = String.join(" ", partialCommandParts);
        final String commandRemainder = String.join(" ", commandRemainderParts);

        if (partialCommand.contains("all_suggestions") || commandRemainder.contains("all_suggestions")) {
            this.parseRecursive(parts);
            return;
        }

        CommandCompletionManager.getCompletion(partialCommand + " ").whenCompleteAsync((suggestions, e) -> {
            if (e != null) {
                logger.warn("Failed to get command completions!", e);
                return;
            }

            final Set<String> completions = getSuggestionContent(suggestions);

            for (final String completion : completions) {
                final String completedCommand = partialCommand + " " + completion + " " + commandRemainder;
                this.queueCommand(completedCommand);
            }
        });
    }

    private void parseRecursive(final String[] parts) {
        final List<Integer> indexes = new LinkedList<>();

        for (int i = 0; i < parts.length; i++) {
            final String part = parts[i];

            if (part.equals("all_suggestions")) {
                indexes.add(i);
            }
        }

        final String[] sections = new String[indexes.size()];

        for (int i = 0; i < indexes.size(); i++) {
            final int beginning = (i == 0) ? 0 : indexes.get(i - 1) + 1;
            final int ending = indexes.get(i);
            final String[] section = Arrays.copyOfRange(parts, beginning, ending);

            sections[i] = String.join(" ", section);
        }

        final List<String> prefixes = new ArrayList<>();
        prefixes.add(""); // dirty hack

        for (int i = 0; i < sections.length; i++) {
            final String section = sections[i];
            final Set<String> newPrefixes = new HashSet<>();

            for (final String prefix : prefixes) {
                final String partialCommand = prefix + section + " ";

                final CompletableFuture<Suggestions> suggestionsFuture = CommandCompletionManager.getCompletion(partialCommand);
                final Suggestions suggestions = suggestionsFuture.join();
                final Set<String> suggestionContent = getSuggestionContent(suggestions)
                        .stream()
                        .map(s -> partialCommand + s + " ")
                        .collect(Collectors.toUnmodifiableSet());
                logger.info("{}", suggestionContent);

                newPrefixes.addAll(suggestionContent);
            }

            prefixes.clear();
            prefixes.addAll(newPrefixes);
        }

        final List<String> commands = prefixes.stream()
                .map(String::trim)
                .toList();
        logger.info("{}", commands.size());

        this.queueCommands(commands);
    }

    private Set<String> getSuggestionContent(final Suggestions suggestions) {
        return suggestions.getList()
                .stream()
                .map(Suggestion::getText)
                .collect(Collectors.toUnmodifiableSet());
    }

    private void queueCommand(final String command) {
        final ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();

        if (networkHandler == null) {
            return;
        }

        commandQueue.add(command);
    }

    private void queueCommands(final List<String> command) {
        commandQueue.addAll(command);
    }

    @Subscribe
    public void onTick(final ClientTickEndEvent event) {
        tickCount++;

        final ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();

        if (networkHandler == null) {
            return;
        }

        final long commandDelay = 90L;
        final long commandDelayInTicks = TimeUtil.getInTicks(commandDelay);

        if (tickCount % commandDelayInTicks != 0) {
            return;
        }


        final String command = this.commandQueue.poll();

        if (command == null) {
            return;
        }

        networkHandler.sendChatCommand(command.trim());
    }

    @Subscribe
    public void onConnectionEnd(final ConnectionEndEvent event) {
        this.commandQueue.clear();
    }

    @Subscribe
    public void onConnectionStart(final ConnectionStartEvent event) {
        this.commandQueue.clear();
    }
}
