package me.allinkdev.deviousmod.command;

import com.mojang.brigadier.CommandDispatcher;
import me.allinkdev.deviousmod.command.impl.ModulesCommand;
import me.allinkdev.deviousmod.command.impl.TestCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;

import java.util.HashSet;
import java.util.Set;

import static me.allinkdev.deviousmod.DeviousMod.logger;

public class CommandManager {
    private static final Set<DCommand> commands = new HashSet<>();

    public static void init() {
        commands.add(new TestCommand());
        commands.add(new ModulesCommand());

        logger.info("Loaded {} commands!", commands.size());
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        for (DCommand command : commands) {
            command.register(dispatcher, registryAccess);
        }
    }
}
