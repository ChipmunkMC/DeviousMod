package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.event.network.packet.impl.PacketC2SEvent;
import me.allinkdev.deviousmod.event.network.packet.impl.PacketS2CEvent;
import me.allinkdev.deviousmod.event.tick.impl.ClientTickEndEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.ModuleManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;

import java.util.HashSet;
import java.util.Set;

public final class ClientsideInventoryModule extends DModule {
    private final Set<Packet<?>> sendNextTick = new HashSet<>();
    private int sendingSlot = -1;

    public ClientsideInventoryModule(final ModuleManager moduleManager) {
        super(moduleManager);
    }

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

        if (stack == ItemStack.EMPTY) {
            if (sendingSlot == slot) {
                sendingSlot = -1;
                return;
            }

            inventory.setStack(networkSlot, ItemStack.EMPTY);
            return;
        }

        if (sendingSlot == slot) {
            sendNextTick.add(new CreativeInventoryActionC2SPacket(networkSlot, ItemStack.EMPTY));
            return;
        }

        event.setCancelled(true);
        inventory.setStack(slot, stack);
    }

    private void interact() {
        final ClientPlayerEntity player = client.player;

        if (player == null) {
            throw new IllegalStateException("Player null!");
        }

        final PlayerInventory inventory = player.getInventory();
        final int selectedSlot = inventory.selectedSlot;
        final ItemStack selectedItem = inventory.getStack(selectedSlot);
        final int networkSlot = selectedSlot + 36;

        final ClientPlayNetworkHandler networkHandler = player.networkHandler;
        sendingSlot = selectedSlot;
        networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(networkSlot, selectedItem));
    }

    @Subscribe
    public void onPacketSend(final PacketC2SEvent event) {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof final CreativeInventoryActionC2SPacket creativeAction) {
            this.processCreativePacket(creativeAction, event);
        } else if (packet instanceof PlayerInteractBlockC2SPacket || packet instanceof PlayerInteractEntityC2SPacket) {
            this.interact();
        }
    }

    @Subscribe
    public void onPacketReceive(final PacketS2CEvent event) {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof InventoryS2CPacket || packet instanceof ScreenHandlerSlotUpdateS2CPacket || packet instanceof UpdateSelectedSlotS2CPacket) {
            event.setCancelled(true);
        }
    }

    @Subscribe
    public void onEndClientTick(final ClientTickEndEvent event) {
        final ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();

        if (networkHandler == null) {
            return;
        }

        final ClientConnection connection = networkHandler.getConnection();

        if (!connection.isOpen()) {
            return;
        }

        for (final Packet<?> packet : Set.copyOf(sendNextTick)) {
            connection.send(packet, PacketCallbacks.always(() -> sendNextTick.remove(packet)));
        }
    }
}
