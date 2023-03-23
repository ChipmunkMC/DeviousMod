package me.allinkdev.deviousmod.command.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;

import java.nio.charset.Charset;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class CharsetArgumentType implements ArgumentType<Charset> {
    private static final Set<String> CHARSET_NAMES = Charset.availableCharsets()
            .values()
            .stream()
            .map(Charset::name)
            .collect(Collectors.toUnmodifiableSet());

    public static <S> Charset getCharset(final CommandContext<S> context, final String name) {
        return context.getArgument(name, Charset.class);
    }

    @Override
    public Charset parse(final StringReader reader) throws CommandSyntaxException {
        final String charsetString = reader.readUnquotedString();

        return Charset.forName(charsetString);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(CHARSET_NAMES, builder);
    }
}
