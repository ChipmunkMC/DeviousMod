package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.event.packet.impl.PacketC2SEvent;
import me.allinkdev.deviousmod.event.packet.impl.PacketS2CEvent;
import me.allinkdev.deviousmod.module.DModule;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;

public class ClientsideInventoryModule extends DModule {

    @Override
    public String getModuleName() {
        return "ClientsideInventory";
    }

    @Override
    public String getDescription() {
        return "Does what it says on the tin. Requires creative mode, else it will not work.";
    }

    private void processCreativePacket(final CreativeInventoryActionC2SPacket creativeAction, final PacketC2SEvent event) {
        final ClientPlayerEntity player = client.player;

        if (player == null) {
            throw new IllegalStateException("Player null!");
        }

        final PlayerInventory inventory = player.getInventory();
        final ItemStack stack = creativeAction.getItemStack();
        final int networkSlot = creativeAction.getSlot();
        final int slot = networkSlot - 36;

        if (inventory.selectedSlot == slot) {
            return;
        }

        if (stack == ItemStack.EMPTY) {
            return;
        }

        event.setCancelled(true);
        inventory.setStack(slot, stack);
    }

    private void processSlotUpdate(final UpdateSelectedSlotC2SPacket packet) {
        final int slot = packet.getSelectedSlot();
        final int networkSlot = slot + 36;

        final ClientPlayerEntity player = client.player;

        if (player == null) {
            throw new IllegalStateException("Player null!");
        }

        final PlayerInventory inventory = player.getInventory();
        final ClientPlayNetworkHandler networkHandler = player.networkHandler;
        inventory.selectedSlot = slot;

        networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(networkSlot, inventory.getStack(slot)));

        for (int i = 0; i < 9; i++) {
            if (i == slot) {
                continue;
            }

            final int loopNetworkSlot = i + 36;
            networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(loopNetworkSlot, ItemStack.EMPTY));
        }
    }

    @Subscribe
    public void onPacketSend(final PacketC2SEvent event) {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof final CreativeInventoryActionC2SPacket creativeAction) {
            this.processCreativePacket(creativeAction, event);
        } else if (packet instanceof final UpdateSelectedSlotC2SPacket updateSlot) {
            this.processSlotUpdate(updateSlot);
        }
    }

    @Subscribe
    public void onPacketReceive(final PacketS2CEvent event) {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof InventoryS2CPacket || packet instanceof ScreenHandlerSlotUpdateS2CPacket || packet instanceof UpdateSelectedSlotS2CPacket) {
            event.setCancelled(true);
        }
    }
}
