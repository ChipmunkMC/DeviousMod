package me.allinkdev.deviousmod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.module.ModuleManager;
import me.allinkdev.deviousmod.util.TextUtil;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

public abstract class DCommand {
    protected final MinecraftClient client = DeviousMod.CLIENT;
    protected final DeviousMod deviousMod;
    protected final ModuleManager moduleManager;

    protected DCommand(final DeviousMod deviousMod) {
        this.deviousMod = deviousMod;
        this.moduleManager = deviousMod.getModuleManager();
    }

    public abstract String getCommandName();

    public LiteralArgumentBuilder<FabricClientCommandSource> getNode() {
        return ClientCommandManager.literal(this.getCommandName())
                .executes(this::execute);
    }

    public void register(final CommandDispatcher<FabricClientCommandSource> dispatcher, final CommandRegistryAccess ignoredRegistryAccess) {
        dispatcher.register(this.getNode());
    }

    public void sendFeedback(final CommandContext<FabricClientCommandSource> context, final Component component) {
        final FabricClientCommandSource source = context.getSource();
        final Text text = TextUtil.toText(component);

        source.sendFeedback(text);
    }

    public abstract int execute(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException;
}
