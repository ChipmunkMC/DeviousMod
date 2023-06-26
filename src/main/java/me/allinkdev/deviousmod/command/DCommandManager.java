package me.allinkdev.deviousmod.command;

import com.github.allinkdev.deviousmod.api.Command;
import com.github.allinkdev.deviousmod.api.managers.CommandManager;
import com.github.allinkdev.reflector.Reflector;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import me.allinkdev.deviousmod.DeviousMod;
import com.github.allinkdev.deviousmod.api.experiments.Experimentality;
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
                .filter(c -> c.getExperimentality() == Experimentality.NONE || (c.getExperimentality() == Experimentality.HIDE && DeviousMod.IS_EXPERIMENTAL))
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
        boolean experimentalLoaded = false;
        final LiteralArgumentBuilder<FabricClientCommandSource> experimentalNode = LiteralArgumentBuilder.literal("experimental");

        for (final DCommand command : this.commands) {
            final Experimentality experimentality = command.getExperimentality();

            if (experimentality == Experimentality.HIDE && !DeviousMod.IS_EXPERIMENTAL) {
                continue;
            } else if (command.getExperimentality() != Experimentality.NONE) {
                experimentalLoaded = true;
                experimentalNode.then(command.getNode());
                continue;
            }

            command.register(dispatcher);
        }

        if (!experimentalLoaded) {
            return;
        }

        dispatcher.register(experimentalNode);
    }
}
