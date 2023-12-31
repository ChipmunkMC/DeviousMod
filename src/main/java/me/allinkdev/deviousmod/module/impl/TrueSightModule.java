package me.allinkdev.deviousmod.module.impl;

import me.allinkdev.deviousmod.event.entity.impl.EntityVisibilityCheckEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import net.lenni0451.lambdaevents.EventHandler;

public final class TrueSightModule extends DModule {
    public TrueSightModule(final DModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    public String getCategory() {
        return "Render";
    }

    @Override
    public String getModuleName() {
        return "TrueSight";
    }

    @Override
    public String getDescription() {
        return "Turns normally transparent entities into translucent ones.";
    }

    @EventHandler
    public void onVisibilityCheck(final EntityVisibilityCheckEvent event) {
        event.setVisible(false);
    }
}
