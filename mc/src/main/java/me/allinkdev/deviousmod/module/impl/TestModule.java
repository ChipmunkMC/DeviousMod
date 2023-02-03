package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.event.entity.living.impl.LivingEntityEquipmentUpdateEvent;
import me.allinkdev.deviousmod.module.DModule;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class TestModule extends DModule {
    @Override
    public String getModuleName() {
        return "Test";
    }

    @Override
    public String getDescription() {
        return "Test 1 2 3 4 5 6 7 8 9 10";
    }

    @Subscribe
    public void onEquipmentUpdate(final LivingEntityEquipmentUpdateEvent event) {
        logger.info("{}", event.getNewStack());
        event.setReplacedStack(new ItemStack(Items.DIRT));
    }
}
