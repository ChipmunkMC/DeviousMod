package me.allinkdev.deviousmod.command;

import com.mojang.brigadier.CommandDispatcher;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.command.impl.BotEvalCommand;
import me.allinkdev.deviousmod.command.impl.KeyringCommand;
import me.allinkdev.deviousmod.command.impl.ModulesCommand;
import me.allinkdev.deviousmod.command.impl.TestCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

import java.util.HashSet;
import java.util.Set;

import static me.allinkdev.deviousmod.DeviousMod.LOGGER;

public final class CommandManager {
    private final Set<DCommand> commands = new HashSet<>();

    public CommandManager(final DeviousMod deviousMod) {
        commands.add(new TestCommand(deviousMod));
        commands.add(new ModulesCommand(deviousMod));
        commands.add(new KeyringCommand(deviousMod));
        commands.add(new BotEvalCommand(deviousMod));

        LOGGER.info("Loaded {} commands!", commands.size());
    }

    public void register(final CommandDispatcher<FabricClientCommandSource> dispatcher, final CommandRegistryAccess registryAccess) {
        for (final DCommand command : this.commands) {
            command.register(dispatcher, registryAccess);
        }
    }
}
