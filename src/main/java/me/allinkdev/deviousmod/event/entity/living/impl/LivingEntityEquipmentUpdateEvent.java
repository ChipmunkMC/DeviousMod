package me.allinkdev.deviousmod.event.entity.living.impl;

import me.allinkdev.deviousmod.event.entity.living.GenericLivingEntityEvent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public final class LivingEntityEquipmentUpdateEvent extends GenericLivingEntityEvent {
    private final ItemStack newStack;
    private final ItemStack oldStack;
    private ItemStack replacedStack;

    public LivingEntityEquipmentUpdateEvent(final LivingEntity entity, final ItemStack newStack, final ItemStack oldStack) {
        super(entity);
        this.newStack = newStack;
        this.oldStack = oldStack;
        this.replacedStack = newStack.copy();
    }

    public ItemStack getNewStack() {
        return this.newStack;
    }

    public ItemStack getOldStack() {
        return this.oldStack;
    }

    public ItemStack getReplacedStack() {
        return this.replacedStack;
    }

    public void setReplacedStack(final ItemStack replacedStack) {
        this.replacedStack = replacedStack;
    }
}
