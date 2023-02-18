package me.allinkdev.deviousmod.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import me.allinkdev.deviousmod.command.DCommand;
import me.allinkdev.deviousmod.command.args.ModuleArgumentType;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.ModuleManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.Set;

public final class ModulesCommand implements DCommand {
    private static final String moduleArgumentName = "module";
    private static final CommandSyntaxException moduleNotFound = new SimpleCommandExceptionType(Text.of("Module not found!"))
            .create();
    private static final CommandSyntaxException moduleAlreadyEnabled = new SimpleCommandExceptionType(Text.of("Module already enabled!"))
            .create();
    private static final CommandSyntaxException moduleAlreadyDisabled = new SimpleCommandExceptionType(Text.of("Module already disabled!"))
            .create();

    private Component getStatusComponent(final boolean state) {
        return state ? Component.text("enabled", NamedTextColor.GREEN) : Component.text("disabled", NamedTextColor.RED);
    }

    @Override
    public String getCommandName() {
        return "modules";
    }

    private DModule genericExecute(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final String argument = StringArgumentType.getString(context, moduleArgumentName);
        final Optional<DModule> moduleOptional = ModuleManager.findModule(argument);

        if (moduleOptional.isEmpty()) {
            throw moduleNotFound;
        }

        return moduleOptional.get();
    }

    private void updateModuleStatus(final CommandContext<FabricClientCommandSource> context, final DModule module, final boolean newState) throws CommandSyntaxException {
        final boolean state = module.getModuleState();

        if (state && newState) {
            throw moduleAlreadyEnabled;
        }

        if (!state && !newState) {
            throw moduleAlreadyDisabled;
        }

        module.setModuleState(newState);
        final String moduleName = module.getModuleName();
        final Component feedback = Component.text(moduleName)
                .append(Component.text(" is now "))
                .append(getStatusComponent(newState))
                .append(Component.text("."));

        sendFeedback(context, feedback);
    }

    private Component constructModuleListStatus(final DModule module) {
        final String name = module.getModuleName();
        final boolean state = module.getModuleState();

        return Component.text(name)
                .append(Component.text(": "))
                .append(getStatusComponent(state));
    }

    public int executeStatus(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final String moduleName = StringArgumentType.getString(context, moduleArgumentName);
        final Optional<DModule> moduleOptional = ModuleManager.findModule(moduleName);

        if (moduleOptional.isEmpty()) {
            throw moduleNotFound;
        }

        final DModule module = moduleOptional.get();

        sendFeedback(context, constructModuleListStatus(module));

        return 1;
    }

    public int executeStatusNoArgs(final CommandContext<FabricClientCommandSource> context) {
        final Set<DModule> modules = ModuleManager.getModules();
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

        sendFeedback(context, component);

        return 1;
    }

    public int executeDisable(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final DModule module = genericExecute(context);
        updateModuleStatus(context, module, false);

        return 1;
    }

    public int executeEnable(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final DModule module = genericExecute(context);
        updateModuleStatus(context, module, true);

        return 1;
    }

    public int executeToggle(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final DModule module = genericExecute(context);
        final boolean moduleState = module.getModuleState();
        updateModuleStatus(context, module, !moduleState);
        return 1;
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getNode() {
        final ModuleArgumentType type = ModuleArgumentType.getType();

        return ClientCommandManager.literal("modules")
                .then(ClientCommandManager.literal("enable")
                        .then(ClientCommandManager.argument(moduleArgumentName, type)
                                .executes(this::executeEnable)))
                .then(ClientCommandManager.literal("disable")
                        .then(ClientCommandManager.argument(moduleArgumentName, type)
                                .executes(this::executeDisable)))
                .then(ClientCommandManager.literal("toggle")
                        .then(ClientCommandManager.argument(moduleArgumentName, type)
                                .executes(this::executeToggle)))
                .then(ClientCommandManager.literal("status")
                        .executes(this::executeStatusNoArgs)
                        .then(ClientCommandManager.argument(moduleArgumentName, type)
                                .executes(this::executeStatus)));
    }

    @Override
    public int execute(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        return 1;
    }
}
