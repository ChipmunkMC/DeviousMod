package me.allinkdev.deviousmod.command.args;

import com.github.allinkdev.deviousmod.api.managers.ModuleManager;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public final class ModuleArgumentType implements ArgumentType<String> {
    private final Set<String> suggestions;

    public ModuleArgumentType(final Set<String> suggestions) {
        this.suggestions = suggestions;
    }

    public static ModuleArgumentType getType(final ModuleManager moduleManager) {
        return new ModuleArgumentType(moduleManager.getModuleNames());
    }

    @Override
    public String parse(final StringReader reader) {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(suggestions, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return suggestions;
    }
}
