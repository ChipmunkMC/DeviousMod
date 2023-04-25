package me.allinkdev.deviousmod.command.impl;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.command.DCommand;
import me.allinkdev.deviousmod.gui.ImGuiScreen;
import me.allinkdev.deviousmod.gui.layer.ClickGuiLayer;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;

public final class TestCommand extends DCommand {
    public TestCommand(final DeviousMod deviousMod) {
        super(deviousMod);
    }

    @Override
    public String getCommandName() {
        return "testing";
    }

    @Override
    public int execute(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final ClickGuiLayer imGuiTestLayer = new ClickGuiLayer(this.deviousMod);
        final ImGuiScreen imGuiScreen = ImGuiScreen.from(imGuiTestLayer);
        final MinecraftClient client = DeviousMod.CLIENT;

        client.execute(() -> client.setScreenAndRender(imGuiScreen));
        return 1;
    }
}
