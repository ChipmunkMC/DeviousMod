package me.allinkdev.deviousmod.keybind;

import me.allinkdev.deviousmod.DeviousMod;
import org.lwjgl.glfw.GLFW;

public abstract class KeyBind {
    protected final DeviousMod deviousMod;

    protected KeyBind(final DeviousMod deviousMod) {
        this.deviousMod = deviousMod;
    }


    public int getDefaultKey() {
        return GLFW.GLFW_KEY_UNKNOWN;
    }

    public abstract String getName();

    public abstract void onPress();
}
