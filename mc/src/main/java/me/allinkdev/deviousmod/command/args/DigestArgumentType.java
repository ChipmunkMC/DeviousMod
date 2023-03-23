package me.allinkdev.deviousmod.command.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.allinkdev.deviousmod.keying.DigestProvider;
import net.minecraft.command.CommandSource;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class DigestArgumentType implements ArgumentType<String> {
    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        final Set<String> availableAlgorithms = DigestProvider.getAvailableAlgorithms();

        return CommandSource.suggestMatching(availableAlgorithms, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return DigestProvider.getExpectedAlgorithms();
    }
}
