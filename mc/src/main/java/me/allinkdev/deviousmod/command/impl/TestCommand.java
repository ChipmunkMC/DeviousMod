package me.allinkdev.deviousmod.command.impl;

import com.mojang.brigadier.context.CommandContext;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.command.DCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;

public final class TestCommand extends DCommand {
    public TestCommand(final DeviousMod deviousMod) {
        super(deviousMod);
    }

    @Override
    public String getCommandName() {
        return "test";
    }

    @Override
    public int execute(final CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.literal("Test!"));

        return 1;
    }
}
