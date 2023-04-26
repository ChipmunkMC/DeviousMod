package me.allinkdev.deviousmod.module.impl;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import me.allinkdev.deviousmod.gui.ImGuiHolder;
import me.allinkdev.deviousmod.gui.ImGuiLayer;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.ModuleManager;
import net.minecraft.SharedConstants;

public final class LogosModule extends DModule implements ImGuiLayer {
    private static final String GAME_VERSION = SharedConstants.getGameVersion().getName();
    private static final String TEXT = "DeviousMod " + GAME_VERSION;

    public LogosModule(final ModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    public String getCategory() {
        return "Render";
    }

    @Override
    public String getModuleName() {
        return "Logos";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public void onEnable() {
        ImGuiHolder.addLayer(this);
    }

    @Override
    public void onDisable() {
        ImGuiHolder.removeLayer(this);
    }

    @Override
    public void process() {
        if (client.options.debugEnabled) {
            return;
        }

        if (client.world == null) {
            return;
        }

        client.inGameHud.setCanShowChatDisabledScreen(true);

        ImGui.begin("internal_logos", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.AlwaysAutoResize);
        ImGui.setWindowPos(2, 2);
        ImGui.text(TEXT);
        ImGui.end();
    }
}
