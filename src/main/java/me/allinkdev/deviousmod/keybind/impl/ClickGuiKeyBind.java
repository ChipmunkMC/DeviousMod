package me.allinkdev.deviousmod.keybind.impl;

import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.gui.ImGuiScreen;
import me.allinkdev.deviousmod.gui.layer.ClickGuiLayer;
import me.allinkdev.deviousmod.keybind.DKeyBind;
import org.lwjgl.glfw.GLFW;

public final class ClickGuiKeyBind extends DKeyBind {
    public ClickGuiKeyBind(final DeviousMod deviousMod) {
        super(deviousMod);
    }

    @Override
    public int getDefaultKey() {
        return GLFW.GLFW_KEY_RIGHT_SHIFT;
    }

    @Override
    public String getName() {
        return "Click GUI";
    }


    @Override
    public void onPress() {
        final ClickGuiLayer clickGuiLayer = new ClickGuiLayer(this.deviousMod);
        final ImGuiScreen imGuiScreen = new ImGuiScreen(clickGuiLayer);

        DeviousMod.CLIENT.setScreen(imGuiScreen);

        super.onPress();
    }
}
