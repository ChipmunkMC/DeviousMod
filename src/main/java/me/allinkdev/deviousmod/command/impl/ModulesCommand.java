package me.allinkdev.deviousmod.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.command.DCommand;
import me.allinkdev.deviousmod.command.args.ModuleArgumentType;
import me.allinkdev.deviousmod.module.DModule;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.Set;

public final class ModulesCommand extends DCommand {
    private static final String MODULE_ARGUMENT_NAME = "module";
    private static final CommandSyntaxException moduleNotFound = new SimpleCommandExceptionType(Text.of("Module not found!"))
            .create();
    private static final CommandSyntaxException moduleAlreadyEnabled = new SimpleCommandExceptionType(Text.of("Module already enabled!"))
            .create();
    private static final CommandSyntaxException moduleAlreadyDisabled = new SimpleCommandExceptionType(Text.of("Module already disabled!"))
            .create();

    public ModulesCommand(final DeviousMod deviousMod) {
        super(deviousMod);
    }

    @Override
    public String getCommandName() {
        return "modules";
    }

    private DModule genericExecute(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final String argument = StringArgumentType.getString(context, MODULE_ARGUMENT_NAME);
        final Optional<DModule> moduleOptional = moduleManager.findModule(argument);

        if (moduleOptional.isEmpty()) {
            throw moduleNotFound;
        }

        return moduleOptional.get();
    }

    private void updateModuleStatus(final DModule module, final boolean newState) throws CommandSyntaxException {
        final boolean state = module.getModuleState();

        if (state && newState) {
            throw moduleAlreadyEnabled;
        }

        if (!state && !newState) {
            throw moduleAlreadyDisabled;
        }

        module.setModuleState(newState);
    }

    private Component constructModuleListStatus(final DModule module) {
        final String name = module.getModuleName();
        final boolean state = module.getModuleState();

        return Component.text(name)
                .append(Component.text(": "))
                .append(DModule.getStatusComponent(state));
    }

    public int executeStatus(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final String moduleName = StringArgumentType.getString(context, MODULE_ARGUMENT_NAME);
        final Optional<DModule> moduleOptional = moduleManager.findModule(moduleName);

        if (moduleOptional.isEmpty()) {
            throw moduleNotFound;
        }

        final DModule module = moduleOptional.get();

        sendFeedback(constructModuleListStatus(module));

        return 1;
    }

    public int executeStatusNoArgs(final CommandContext<FabricClientCommandSource> context) {
        final Set<DModule> modules = moduleManager.getModules();
        int enabled = 0;
        Component component = Component.text("Module states:")
                .append(Component.newline());

        for (final DModule module : modules) {
            final boolean state = module.getModuleState();
            component = component.append(Component.text(" - "))
                    .append(constructModuleListStatus(module))
                    .append(Component.newline());

            if (!state) {
                continue;
            }

            enabled++;
        }

        component = component
                .append(Component.newline())
                .append(Component.text(enabled))
                .append(Component.text(" enabled modules out of "))
                .append(Component.text(modules.size()))
                .append(Component.text(" total modules."));

        sendFeedback(component);

        return 1;
    }

    public int executeList(final CommandContext<FabricClientCommandSource> context) {
        final Set<DModule> modules = moduleManager.getModules();

        Component component = Component.text("Available modules:")
                .append(Component.newline());

        for (final DModule module : moduleManager.getModules()) {
            final String name = module.getModuleName();
            component = component.append(Component.text(" - ")
                            .append(Component.text(name)))
                    .append(Component.newline());
        }

        component = component
                .append(Component.newline())
                .append(Component.text("In total, there are "))
                .append(Component.text(modules.size()))
                .append(Component.text(" available modules."));

        sendFeedback(component);
        return 1;
    }

    public int executeDisable(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final DModule module = genericExecute(context);
        updateModuleStatus(module, false);

        return 1;
    }

    public int executeEnable(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final DModule module = genericExecute(context);
        updateModuleStatus(module, true);

        return 1;
    }

    public int executeToggle(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final DModule module = genericExecute(context);
        final boolean moduleState = module.getModuleState();
        updateModuleStatus(module, !moduleState);
        return 1;
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getNode() {
        final ModuleArgumentType type = ModuleArgumentType.getType();

        return ClientCommandManager.literal("modules")
                .then(ClientCommandManager.literal("enable")
                        .then(ClientCommandManager.argument(MODULE_ARGUMENT_NAME, type)
                                .executes(this::executeEnable)))
                .then(ClientCommandManager.literal("disable")
                        .then(ClientCommandManager.argument(MODULE_ARGUMENT_NAME, type)
                                .executes(this::executeDisable)))
                .then(ClientCommandManager.literal("toggle")
                        .then(ClientCommandManager.argument(MODULE_ARGUMENT_NAME, type)
                                .executes(this::executeToggle)))
                .then(ClientCommandManager.literal("status")
                        .executes(this::executeStatusNoArgs)
                        .then(ClientCommandManager.argument(MODULE_ARGUMENT_NAME, type)
                                .executes(this::executeStatus)))
                .then(ClientCommandManager.literal("list")
                        .executes(this::executeList));
    }

    @Override
    public int run(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        return 1;
    }
}
