package me.allinkdev.deviousmod.module.impl;

import me.allinkdev.deviousmod.event.input.ItemUseCooldownCheckEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import net.lenni0451.lambdaevents.EventHandler;

public final class NoItemUseCooldownModule extends DModule {
    public NoItemUseCooldownModule(final DModuleManager moduleManager) {
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

    @EventHandler
    public void onItemUseCooldownCheck(final ItemUseCooldownCheckEvent event) {
        event.setValue(0);
    }
}
