package me.allinkdev.deviousmod.module.impl;

import com.github.allinkdev.deviousmod.api.gui.ImGuiLayer;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import net.minecraft.SharedConstants;

public final class LogosModule extends DModule implements ImGuiLayer {
    private static final String GAME_VERSION = SharedConstants.getGameVersion().getName();
    private static final String TEXT = "DeviousMod " + GAME_VERSION;

    public LogosModule(final DModuleManager moduleManager) {
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
        return "Displays a DeviousMod watermark in the top left of your screen.";
    }

    @Override
    public void onEnable() {
        DeviousMod.getInstance().getImGuiHolder().addLayer(this);
    }

    @Override
    public void onDisable() {
        DeviousMod.getInstance().getImGuiHolder().removeLayer(this);
    }

    @Override
    public void process() {
        if (client.options.debugEnabled || client.world == null) return;

        ImGui.begin("internal_logos", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.AlwaysAutoResize);
        ImGui.setWindowPos(4, 4);
        ImGui.textColored(0, 0, 0, 255, TEXT);
        ImGui.end();

        ImGui.begin("internal_logos_shadow", ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoBackground | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.AlwaysAutoResize);
        ImGui.setWindowPos(2, 2);
        ImGui.text(TEXT);
        ImGui.end();
    }
}
