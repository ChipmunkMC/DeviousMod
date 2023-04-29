package me.allinkdev.deviousmod.command.impl;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.command.DCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;

public final class GetNBTCommand extends DCommand {
    public GetNBTCommand(final DeviousMod deviousMod) {
        super(deviousMod);
    }

    @Override
    public String getCommandName() {
        return "getnbt";
    }

    @Override
    public int execute(final CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        final MinecraftClient client = DeviousMod.CLIENT;
        final ClientPlayerEntity player = client.player;

        if (player == null) {
            return 0;
        }

        final PlayerInventory playerInventory = player.getInventory();

        if (playerInventory == null) {
            return 0;
        }

        final ItemStack heldItem = playerInventory.getMainHandStack();
        final DefaultedList<ItemStack> offHandItems = playerInventory.offHand;

        boolean allEmpty = true;
        final boolean holdingItemInMain = !heldItem.isEmpty();

        if (holdingItemInMain) {
            allEmpty = false;
        }

        List<Pair<ItemStack, Integer>> nonEmptyOffHandItems = null;
        boolean holdingStuffInOffHand = false;

        for (int i = 0; i < offHandItems.size(); i++) {
            final ItemStack offHandItem = offHandItems.get(i);

            if (offHandItem.isEmpty()) {
                continue;
            }

            allEmpty = false;
            holdingStuffInOffHand = true;

            if (nonEmptyOffHandItems == null) {
                nonEmptyOffHandItems = new ArrayList<>();
            }

            final Pair<ItemStack, Integer> pair = new Pair<>(offHandItem, i);
            nonEmptyOffHandItems.add(pair);
        }

        final List<Component> components = new ArrayList<>();

        if (allEmpty) {
            components.add(Component.text("You have no items in your hands!", NamedTextColor.GRAY));
        } else {
            if (holdingItemInMain) {
                final Component nbtComponent = this.getComponent(heldItem);

                components.add(Component.text("Main hand: ", NamedTextColor.GRAY)
                        .append(nbtComponent));
            }

            if (holdingStuffInOffHand) {
                for (final Pair<ItemStack, Integer> nonEmptyOffHandItem : nonEmptyOffHandItems) {
                    final ItemStack itemStack = nonEmptyOffHandItem.getLeft();
                    final int index = nonEmptyOffHandItem.getRight();

                    final Component nbtComponent = this.getComponent(itemStack);
                    components.add(Component.text("Offhand item #", NamedTextColor.GRAY)
                            .append(Component.text(index, NamedTextColor.GRAY))
                            .append(Component.text(": ", NamedTextColor.GRAY))
                            .append(nbtComponent));
                }
            }
        }

        final Component component = Component.join(JoinConfiguration.newlines(), components);

        this.sendFeedback(context, component);
        return 1;
    }

    private Component getComponent(final ItemStack itemStack) {
        final NbtCompound compound = itemStack.getOrCreateNbt();
        final Text prettyPrintedNBT = NbtHelper.toPrettyPrintedText(compound);

        return prettyPrintedNBT.asComponent().color(NamedTextColor.WHITE);
    }
}
