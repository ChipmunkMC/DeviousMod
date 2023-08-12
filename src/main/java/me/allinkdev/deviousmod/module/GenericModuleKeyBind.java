package me.allinkdev.deviousmod.module;

import com.github.allinkdev.deviousmod.api.module.Module;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.keybind.DKeyBind;

public final class GenericModuleKeyBind extends DKeyBind {
    private final Module module;

    public GenericModuleKeyBind(final DeviousMod deviousMod, final Module module) {
        super(deviousMod);

        this.module = module;
    }

    @Override
    public String getName() {
        return module.getModuleName();
    }

    @Override
    public void onPress() {
        this.deviousMod.getModuleManager().updateModuleState(module, !module.getModuleState(), true);

        super.onPress();
    }
}
