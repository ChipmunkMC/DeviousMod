package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.event.entity.impl.EntityRemoveEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.ModuleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;

public final class KeepCorpsesModule extends DModule {
    public KeepCorpsesModule(final ModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    public String getCategory() {
        return "Miscellaneous";
    }

    @Override
    public String getModuleName() {
        return "KeepCorpses";
    }

    @Override
    public String getDescription() {
        return "Don't remove entity corpses on death. May trigger anticheats.";
    }

    @Subscribe
    public void onEntityRemove(final EntityRemoveEvent event) {
        final Entity entity = event.getEntity();

        if (entity.isAlive()) {
            return;
        }

        if (!(entity instanceof LivingEntity)) {
            return;
        }

        event.setCancelled(true);
    }
}
