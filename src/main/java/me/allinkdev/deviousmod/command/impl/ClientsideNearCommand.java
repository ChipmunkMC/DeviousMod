package me.allinkdev.deviousmod.command.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.command.DCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;

import java.util.ArrayList;
import java.util.List;

public final class ClientsideNearCommand extends DCommand {
    public ClientsideNearCommand(final DeviousMod deviousMod) {
        super(deviousMod);
    }

    @Override
    public String getCommandName() {
        return "cnear";
    }

    private void computeDistance(final ClientPlayerEntity player, final AbstractClientPlayerEntity entity, final List<Component> distanceComponents) {
        final double distance = Math.floor(player.squaredDistanceTo(entity));
        final GameProfile profile = entity.getGameProfile();
        final String username = profile.getName();

        final Component component = Component.text(username, NamedTextColor.WHITE)
                .append(Component.space())
                .append(Component.text("(", NamedTextColor.GRAY))
                .append(Component.text(distance, NamedTextColor.GRAY))
                .append(Component.text("m)", NamedTextColor.GRAY));

        distanceComponents.add(component);
    }

    @Override
    public int run(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final MinecraftClient client = DeviousMod.CLIENT;
        final ClientWorld world = client.world;

        if (world == null) {
            return 0;
        }

        final ClientPlayerEntity player = client.player;

        if (player == null) {
            return 0;
        }

        final List<AbstractClientPlayerEntity> players = world.getPlayers()
                .stream()
                .filter(p -> !p.equals(player))
                .toList();
        final List<Component> distanceComponents = new ArrayList<>();

        players.forEach(p -> this.computeDistance(player, p, distanceComponents));

        final Component joinedDistanceComponents = Component.join(JoinConfiguration.commas(true), distanceComponents);
        final Component feedback = Component.text("Nearby players: ", NamedTextColor.GRAY)
                .append(joinedDistanceComponents);

        this.sendFeedback(context, feedback);
        return 1;
    }
}
