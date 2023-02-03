package me.allinkdev.deviousmod.command.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.allinkdev.deviousmod.module.ModuleManager;
import net.minecraft.command.CommandSource;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModuleArgumentType implements ArgumentType<String> {
    private static Set<String> SUGGESTIONS = ModuleManager.getModuleNames();

    public static ModuleArgumentType getType() {
        return new ModuleArgumentType();
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(SUGGESTIONS, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return SUGGESTIONS;
    }
}