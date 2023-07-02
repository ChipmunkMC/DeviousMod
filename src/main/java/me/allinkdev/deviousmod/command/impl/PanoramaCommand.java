package me.allinkdev.deviousmod.command.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.command.DCommand;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PanoramaCommand extends DCommand {
    public PanoramaCommand(final DeviousMod deviousMod) {
        super(deviousMod);
    }

    @Override
    public String getCommandName() {
        return "panorama";
    }

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> getNode() {
        return ClientCommandManager.literal("panorama")
                .executes(this)
                .then(ClientCommandManager.argument("resolution", IntegerArgumentType.integer()).executes(this::runWithRes));
    }

    private File allocateDirectory() {
        final Path path = Path.of("data", "deviousmod", "panorama", String.valueOf(System.currentTimeMillis()));
        final Path screenshotPath = path.resolve("screenshots");

        if (!Files.exists(screenshotPath)) {
            try {
                Files.createDirectories(screenshotPath);
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to create screenshot path", e);
            }
        }

        return path.toFile();
    }

    private void capturePanorama(final CommandContext<FabricClientCommandSource> context, final int res) {
        final FabricClientCommandSource source = context.getSource();
        final File directory = this.allocateDirectory();
        source.getClient().takePanorama(directory, res, res);
        final String directoryPath = directory.getAbsolutePath();
        this.sendFeedback(Component.text("Captured panorama. ").append(Component.text("Click here to open the folder.")
                .decorate(TextDecoration.UNDERLINED)
                .clickEvent(ClickEvent.openFile(directory.getAbsolutePath()))
                .hoverEvent(HoverEvent.showText(Component.text(directoryPath))))
        );
    }

    private int runWithRes(final CommandContext<FabricClientCommandSource> context) {
        this.capturePanorama(context, IntegerArgumentType.getInteger(context, "resolution"));
        return 1;
    }

    @Override
    public int run(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        this.capturePanorama(context, 1000);
        return 1;
    }
}
