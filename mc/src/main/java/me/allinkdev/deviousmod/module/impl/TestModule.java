package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.event.entity.impl.AddEntityEvent;
import me.allinkdev.deviousmod.event.entity.impl.RemoveEntityEvent;
import me.allinkdev.deviousmod.module.DModule;

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
    public void onEntityAdd(final AddEntityEvent event) {
        logger.info("{} added!", event.getEntity().getEntityName());
        event.setCancelled(true);
    }

    @Subscribe
    public void onEntityRemove(final RemoveEntityEvent event) {
        logger.info("{} removed!", event.getEntity().getEntityName());
    }
}
