package me.allinkdev.deviousmod.command;

import com.github.allinkdev.deviousmod.api.Command;
import com.github.allinkdev.deviousmod.api.managers.CommandManager;
import com.github.allinkdev.reflector.Reflector;
import com.mojang.brigadier.CommandDispatcher;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.command.impl.TestCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

import java.util.*;

public final class DCommandManager implements CommandManager<FabricClientCommandSource> {
    private final Set<DCommand> commands = new HashSet<>();

    public DCommandManager(final DeviousMod deviousMod) {
        final List<? extends DCommand> newCommands = Reflector.createNew(DCommand.class)
                .allSubClassesInSubPackage("impl")
                .map(Reflector::createNew)
                .map(r -> r.create(deviousMod))
                .map(Optional::orElseThrow)
                .filter(c -> !(c instanceof TestCommand) || DeviousMod.isDevelopment())
                .toList();

        commands.addAll(newCommands);

        DeviousMod.LOGGER.info("Loaded {} commands!", commands.size());
    }

    public void register(final CommandDispatcher<FabricClientCommandSource> dispatcher, final CommandRegistryAccess registryAccess) {
        this.register(dispatcher);
    }

    @Override
    public Set<Command<FabricClientCommandSource>> getCommands() {
        return Collections.unmodifiableSet(this.commands);
    }

    @Override
    public void register(final CommandDispatcher<FabricClientCommandSource> dispatcher) {
        for (final DCommand command : this.commands) {
            command.register(dispatcher);
        }
    }
}
