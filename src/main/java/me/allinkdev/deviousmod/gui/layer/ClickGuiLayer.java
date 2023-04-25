package me.allinkdev.deviousmod.gui.layer;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.gui.AbstractImGuiLayer;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.ModuleManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public final class ClickGuiLayer extends AbstractImGuiLayer {
    private final Map<String, Set<DModule>> categoryModuleMap = new HashMap<>();

    public ClickGuiLayer(final DeviousMod deviousMod) {
        super(deviousMod);
    }

    private void registerModule(final DModule module) {
        final String category = module.getCategory();
        final boolean shouldPut;
        final Set<DModule> moduleSet;

        if (!categoryModuleMap.containsKey(category)) {
            moduleSet = new HashSet<>();
            shouldPut = true;
        } else {
            moduleSet = categoryModuleMap.get(category);
            shouldPut = false;
        }

        moduleSet.add(module);

        if (shouldPut) {
            categoryModuleMap.put(category, moduleSet);
        }
    }

    @Override
    public void init() {
        if (this.deviousMod == null) {
            throw new IllegalStateException("DeviousMod somehow null!");
        }

        final ModuleManager moduleManager = this.deviousMod.getModuleManager();
        final Set<DModule> modules = moduleManager.getModules();
        modules.forEach(this::registerModule);
    }

    private void renderButton(final DModule module) {
        final String moduleName = module.getModuleName();
        final float buttonWidth = ImGui.getWindowWidth();
        final float buttonHeight = ImGui.getTextLineHeight();
        final boolean isToggled = module.getModuleState();
        final String suffix = isToggled ? " (on)" : " (off)"; // TODO: Replace with a colour or something

        final boolean clicked = ImGui.button(moduleName + suffix, buttonWidth - 18, buttonHeight + 3);
        
        if (!clicked) {
            return;
        }

        final boolean moduleState = module.getModuleState();
        module.setModuleState(!moduleState);
    }

    private void renderCategory(final AtomicInteger offsetAtomic, final String name, final Set<DModule> modules) {
        ImGui.begin(name);

        modules.forEach(this::renderButton);

        final int windowWidth = (int) ImGui.getWindowWidth();
        final int windowHeight = (int) ImGui.getWindowHeight();
        final int offset = offsetAtomic.addAndGet(30);
        ImGui.setWindowPos(offset, 10, ImGuiCond.FirstUseEver);
        final float newWindowWidth = windowWidth * 6.5F;
        final float newWindowHeight = windowHeight * 4.5F;

        ImGui.setWindowSize(newWindowWidth, newWindowHeight, ImGuiCond.FirstUseEver);
        offsetAtomic.set(offset + (int) newWindowWidth);

        ImGui.end();
    }

    @Override
    public void process() {
        final AtomicInteger offset = new AtomicInteger(0);

        this.categoryModuleMap.forEach((name, modules) -> this.renderCategory(offset, name, modules));
    }
}