package me.allinkdev.deviousmod.module;

import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.keybind.KeyBind;

public final class GenericModuleKeyBind extends KeyBind {
    private final DModule module;

    public GenericModuleKeyBind(final DeviousMod deviousMod, final DModule module) {
        super(deviousMod);

        this.module = module;
    }

    @Override
    public String getName() {
        return module.getModuleName();
    }

    @Override
    public void onPress() {
        module.toggle();
    }
}
