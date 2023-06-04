package me.allinkdev.deviousmod.gui.layer;

import com.github.allinkdev.deviousmod.api.module.Module;
import com.google.common.eventbus.Subscribe;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.tick.impl.ClientTickEndEvent;
import me.allinkdev.deviousmod.gui.AbstractImGuiLayer;
import me.allinkdev.deviousmod.module.DModuleManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public final class ClickGuiLayer extends AbstractImGuiLayer {
    private final Map<String, Set<Module>> categoryModuleMap = new HashMap<>();
    private final Map<Module, Boolean> moduleToggleMap = new HashMap<>();

    public ClickGuiLayer(final DeviousMod deviousMod) {
        super(deviousMod);
    }

    private void registerModule(final Module module) {
        final String category = module.getCategory();
        final boolean shouldPut;
        final Set<Module> moduleSet;

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
    public void layerInit() {
        if (this.deviousMod == null) {
            throw new IllegalStateException("DeviousMod somehow null!");
        }

        final DModuleManager moduleManager = this.deviousMod.getModuleManager();
        final Set<Module> modules = moduleManager.getModules();
        modules.forEach(this::registerModule);

        this.deviousMod.subscribeEvents(this);
    }

    private void renderButton(final Module module) {
        final String moduleName = module.getModuleName();
        final float buttonWidth = ImGui.getWindowWidth();
        final float buttonHeight = ImGui.getTextLineHeight();
        final boolean isToggled = module.getModuleState();
        final String suffix = isToggled ? " (on)" : " (off)"; // TODO: Replace with a colour or something

        final boolean clicked = ImGui.button(moduleName + suffix, buttonWidth - 18, buttonHeight + 3);
        final boolean hovered = ImGui.isItemHovered();

        if (hovered) {
            ImGui.beginTooltip();

            final String moduleDescription = module.getDescription();
            ImGui.text(moduleDescription);

            ImGui.endTooltip();
        }

        if (!clicked) {
            return;
        }

        final boolean moduleState = module.getModuleState();
        moduleToggleMap.put(module, !moduleState);
    }

    private void renderCategory(final AtomicInteger offsetAtomic, final String name, final Set<Module> modules) {
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

    @Subscribe
    public void onTick(final ClientTickEndEvent event) {
        if (this.moduleToggleMap.isEmpty()) {
            return;
        }

        this.moduleToggleMap.forEach(Module::setModuleState);
        this.moduleToggleMap.clear();
    }
}
