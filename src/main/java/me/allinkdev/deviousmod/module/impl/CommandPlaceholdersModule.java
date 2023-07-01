package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.command.CommandCompletionManager;
import me.allinkdev.deviousmod.event.self.chat.impl.SelfSendCommandEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import me.allinkdev.deviousmod.queue.CommandQueueManager;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class CommandPlaceholdersModule extends DModule {
    private static final String SEARCH_STRING = "all_suggestions";
    private final List<String> sending = Collections.synchronizedList(new ArrayList<>());
    private final CommandQueueManager commandQueueManager;

    public CommandPlaceholdersModule(final DModuleManager moduleManager) {
        super(moduleManager);
        this.commandQueueManager = DeviousMod.getInstance().getCommandQueueManager();
    }

    @Override
    public String getCategory() {
        return "Miscellaneous";
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
        if (event.wasQueued()) {
            return;
        }

        final String command = event.getMessage();

        if (!command.contains(SEARCH_STRING)) {
            return;
        }

        if (this.sending.contains(command)) {
            this.sending.remove(command);
            return;
        }

        event.cancel();
        final String trimmed = command.trim();
        final String[] parts = trimmed.split(" ");
        int partIndex = -1;

        for (int i = 0; i < parts.length; i++) {
            final String element = parts[i];

            if (element.equals(SEARCH_STRING)) {
                if (partIndex != -1) {
                    this.parseRecursive(parts);
                    return;
                }

                partIndex = i;
            }
        }

        if (partIndex == -1) {
            logger.warn("Part index is -1!");
            return;
        }

        final String[] partialCommandParts = Arrays.copyOfRange(parts, 0, partIndex);
        final String[] commandRemainderParts = Arrays.copyOfRange(parts, partIndex + 1, parts.length);
        final String partialCommand = String.join(" ", partialCommandParts);
        final String commandRemainder = String.join(" ", commandRemainderParts);

        if (partialCommand.contains(SEARCH_STRING) || commandRemainder.contains(SEARCH_STRING)) {
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
                this.commandQueueManager.addCommandToBack(completedCommand);
            }
        });
    }

    private void parseRecursive(final String[] parts) {
        final List<Integer> indexes = new LinkedList<>();

        for (int i = 0; i < parts.length; i++) {
            final String part = parts[i];

            if (part.equals(SEARCH_STRING)) {
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

                newPrefixes.addAll(suggestionContent);
            }

            prefixes.clear();
            prefixes.addAll(newPrefixes);
        }

        final List<String> commands = prefixes.stream()
                .map(String::trim)
                .toList();

        this.commandQueueManager.addCommandsToBack(commands);
    }

    private Set<String> getSuggestionContent(final Suggestions suggestions) {
        return suggestions.getList()
                .stream()
                .map(Suggestion::getText)
                .collect(Collectors.toUnmodifiableSet());
    }
}