package me.allinkdev.deviousmod.command;

import com.github.allinkdev.reflector.Reflector;
import com.mojang.brigadier.CommandDispatcher;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.command.impl.TestCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static me.allinkdev.deviousmod.DeviousMod.LOGGER;

public final class CommandManager {
    private final Set<DCommand> commands = new HashSet<>();

    public CommandManager(final DeviousMod deviousMod) {
        final List<? extends DCommand> newCommands = Reflector.createNew(DCommand.class)
                .allSubClassesInSubPackage("impl")
                .map(Reflector::createNew)
                .map(r -> r.create(deviousMod))
                .map(Optional::orElseThrow)
                .filter(c -> !(c instanceof TestCommand) || DeviousMod.isDevelopment())
                .toList();

        commands.addAll(newCommands);

        LOGGER.info("Loaded {} commands!", commands.size());
    }

    public void register(final CommandDispatcher<FabricClientCommandSource> dispatcher, final CommandRegistryAccess registryAccess) {
        for (final DCommand command : this.commands) {
            command.register(dispatcher, registryAccess);
        }
    }
}
