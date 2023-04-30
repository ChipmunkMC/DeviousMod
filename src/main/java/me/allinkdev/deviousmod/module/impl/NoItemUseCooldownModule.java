package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.event.input.ItemUseCooldownCheckEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.ModuleManager;

public final class NoItemUseCooldownModule extends DModule {
    public NoItemUseCooldownModule(final ModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    public String getCategory() {
        return "Miscellaneous";
    }

    @Override
    public String getModuleName() {
        return "NoItemUseCooldown";
    }

    @Override
    public String getDescription() {
        return "Disables the item use cool down.";
    }

    @Subscribe
    public void onItemUseCooldownCheck(final ItemUseCooldownCheckEvent event) {
        event.setValue(0);
    }
}
