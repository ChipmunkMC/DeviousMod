package me.allinkdev.deviousmod.command.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.keying.BotKey;
import me.allinkdev.deviousmod.keying.BotKeyProvider;
import net.minecraft.command.CommandSource;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class BotKeyArgumentType implements ArgumentType<BotKey> {
    private static final DeviousMod DEVIOUS_MOD = DeviousMod.getInstance();

    public static <S> BotKey getBotKey(final CommandContext<S> context, final String name) {
        return context.getArgument(name, BotKey.class);
    }

    @Override
    public BotKey parse(final StringReader reader) throws CommandSyntaxException {
        final BotKeyProvider botKeyProvider = DEVIOUS_MOD.getBotKeyProvider();
        final String name = reader.readUnquotedString();
        final Optional<BotKey> botKeyOptional = botKeyProvider.findKey(name);

        return botKeyOptional.orElse(null);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        final BotKeyProvider botKeyProvider = DEVIOUS_MOD.getBotKeyProvider();
        final Set<String> suggestions = botKeyProvider.getLoadedKeys()
                .stream()
                .map(BotKey::getIdentifier)
                .collect(Collectors.toUnmodifiableSet());

        return CommandSource.suggestMatching(suggestions, builder);
    }

    // There are no examples
    @Override
    public Collection<String> getExamples() {
        return Collections.emptySet();
    }
}
