package me.allinkdev.deviousmod.module.impl;

import me.allinkdev.deviousmod.event.network.packet.impl.PacketC2SEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import net.lenni0451.lambdaevents.EventHandler;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;

public final class ClientsideBookReaderModule extends DModule {
    public ClientsideBookReaderModule(final DModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    public String getModuleName() {
        return "ClientsideBookViewer";
    }

    @Override
    public String getDescription() {
        return "Views books fully clientside to evade TotalFreedomMod restrictions on the reading of books.";
    }

    private boolean isWrittenBook(final ItemStack itemStack) {
        return itemStack.getItem().equals(Items.WRITTEN_BOOK);
    }

    @Override
    public String getCategory() {
        return "TotalFreedom";
    }

    @EventHandler
    public void onPacketSend(final PacketC2SEvent event) {
        final ClientPlayerEntity clientPlayerEntity = this.client.player;

        if (clientPlayerEntity == null) {
            return;
        }

        final PlayerInventory playerInventory = clientPlayerEntity.getInventory();
        final ItemStack mainStack = playerInventory.main.stream().filter(this::isWrittenBook).findFirst().orElse(null);
        final ItemStack offhandStack = playerInventory.offHand.stream().filter(this::isWrittenBook).findFirst().orElse(null);
        final boolean holdingBook = mainStack != null || offhandStack != null;

        final Packet<?> packet = event.getPacket();
        if (!(packet instanceof PlayerInteractBlockC2SPacket) && !(packet instanceof PlayerInteractItemC2SPacket) || !holdingBook) {
            return;
        }
        event.setCancelled(true);

        final ItemStack heldBook = (mainStack == null ? offhandStack : mainStack).copy();
        final BookScreen bookScreen = new BookScreen(new BookScreen.WrittenBookContents(heldBook), false);
        this.client.executeSync(() -> this.client.setScreen(bookScreen));
    }
}
