package me.allinkdev.deviousmod.command.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public final class KeyTemplateArgumentType implements ArgumentType<String> {
    private boolean isAllowedInTemplate(final char character) {
        if (character != ' ') {
            return true;
        }

        return false;
    }

    /**
     * Shamelessly stolen from {@link StringReader#readUnquotedString()}
     **/
    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        while (reader.canRead() && isAllowedInTemplate(reader.peek())) {
            reader.skip();
        }

        return reader.getRead().substring(start, reader.getCursor());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CompletableFuture.completedFuture(builder.build());
    }

    @Override
    public Collection<String> getExamples() {
        return Collections.emptySet();
    }
}
