package me.allinkdev.deviousmod.mixin.client.entity;

import me.allinkdev.deviousmod.event.entity.living.impl.LivingEntityEquipmentUpdateEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(ClientPlayNetworkHandler.class)
public class EntityEquipmentOverrider {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private ClientWorld world;

    /**
     * @author Allink
     * @reason Allow overriding of entity equipment updates
     */
    @Overwrite
    public void onEntityEquipmentUpdate(EntityEquipmentUpdateS2CPacket packet) {
        NetworkThreadUtils.forceMainThread(packet, (ClientPlayPacketListener) (Object) this, this.client);
        final Entity entity = this.world.getEntityById(packet.getId());

        if (entity == null) {
            return;
        }

        if (!(entity instanceof final LivingEntity livingEntity)) {
            return;
        }

        for (com.mojang.datafixers.util.Pair<EquipmentSlot, ItemStack> pair : packet.getEquipmentList()) {
            final EquipmentSlot slot = pair.getFirst();
            final ItemStack oldStack = livingEntity.getEquippedStack(slot);
            final ItemStack newStack = pair.getSecond();
            final Optional<ItemStack> overrideOptional = LivingEntityEquipmentUpdateEvent.updateEntityEquipment(livingEntity, newStack, oldStack);

            if (overrideOptional.isEmpty()) {
                continue;
            }

            final ItemStack override = overrideOptional.get();

            livingEntity.equipStack(slot, override);
        }
    }
}
