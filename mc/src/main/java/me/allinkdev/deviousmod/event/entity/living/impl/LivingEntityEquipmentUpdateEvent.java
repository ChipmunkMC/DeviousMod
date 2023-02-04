package me.allinkdev.deviousmod.event.entity.living.impl;

import lombok.Getter;
import lombok.Setter;
import me.allinkdev.deviousmod.event.entity.living.GenericLivingEntityEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

@Getter
public class LivingEntityEquipmentUpdateEvent extends GenericLivingEntityEvent {
    private final ItemStack newStack;
    private final ItemStack oldStack;
    @Setter
    private ItemStack replacedStack;

    public LivingEntityEquipmentUpdateEvent(final LivingEntity entity, final ItemStack newStack, final ItemStack oldStack) {
        super(entity);
        this.newStack = newStack;
        this.oldStack = oldStack;
        this.replacedStack = newStack.copy();
    }
}
