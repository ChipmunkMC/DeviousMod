package me.allinkdev.deviousmod.gui.layer;

import com.github.allinkdev.deviousmod.api.experiments.Experimentality;
import com.github.allinkdev.deviousmod.api.module.Module;
import com.google.common.eventbus.Subscribe;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiTreeNodeFlags;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.time.tick.impl.ClientTickEndEvent;
import me.allinkdev.deviousmod.gui.AbstractImGuiLayer;
import me.allinkdev.deviousmod.gui.widget.SettingsWidget;
import me.allinkdev.deviousmod.module.DModuleManager;
import me.allinkdev.deviousmod.util.EventUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public final class ClickGuiLayer extends AbstractImGuiLayer {
    // TODO: Less maps, please
    private final Map<String, Set<Module>> categoryModuleMap = new HashMap<>();
    private final Map<Module, Boolean> moduleToggleMap = new HashMap<>();
    private final Map<Module, SettingsWidget> settingsWidgetMap = new HashMap<>();
    private final Set<Module> openSettings = new ObjectArraySet<>();

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

        final boolean hasSettings = module.getSettings().getKeys().size() > 1;

        if (!hasSettings) {
            return;
        }

        this.settingsWidgetMap.put(module, new SettingsWidget(module));
    }

    @Override
    public void layerInit() {
        if (this.deviousMod == null) {
            throw new IllegalStateException("DeviousMod somehow null!");
        }

        final DModuleManager moduleManager = this.deviousMod.getModuleManager();
        final Set<Module> modules = moduleManager.getModules();
        modules.stream().filter(m -> ((m.getExperimentality() == Experimentality.NONE) || m.getExperimentality() == Experimentality.WARN) || (m.getExperimentality() == Experimentality.HIDE && DeviousMod.IS_EXPERIMENTAL)).forEach(this::registerModule);

        EventUtil.registerListener(this);
    }

    private void renderModule(final Module module) {
        final String moduleName = module.getModuleName();
        final boolean isToggled = module.getModuleState();
        final boolean experimental = module.getExperimentality() != Experimentality.NONE;
        final String suffix = (isToggled ? " (on)" : " (off)") + (experimental ? " (dev)" : ""); // TODO: Replace with a colour or something

        final SettingsWidget settingsWidget = this.settingsWidgetMap.get(module);
        final boolean hasSettings = settingsWidget != null;
        int flags = ImGuiTreeNodeFlags.Framed | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow;

        if (!hasSettings) {
            flags = flags | ImGuiTreeNodeFlags.Leaf;
        }

        if (experimental) {
            ImGui.pushStyleColor(ImGuiCol.Header, 100, 10, 14, 255);
            ImGui.pushStyleColor(ImGuiCol.HeaderHovered, 255, 22, 48, 255);
        }

        final boolean isOpen = ImGui.treeNodeEx(moduleName + suffix, flags);
        if (experimental) {
            ImGui.popStyleColor(2);
        }
        final boolean clicked;

        if (!hasSettings) {
            clicked = ImGui.isItemClicked();
        } else {
            final boolean wasOpen = this.openSettings.contains(module);
            clicked = ImGui.isItemClicked() && !isOpen && !wasOpen;

            if (wasOpen && !isOpen) {
                this.openSettings.remove(module);
            } else if (isOpen) {
                this.openSettings.add(module);
            }
        }

        final boolean hovered = ImGui.isItemHovered();

        if (hovered) {
            ImGui.beginTooltip();
            final StringBuilder tooltip = new StringBuilder(module.getDescription());

            if (experimental) {
                tooltip.append("\n\n").append("Experiment Description: ").append(module.getExperimentDescription().orElse("None provided."));
            }

            ImGui.text(tooltip.toString());
            ImGui.endTooltip();
        }

        if (isOpen) {
            if (hasSettings) {
                this.renderSettings(settingsWidget);
            }

            ImGui.treePop();
        }

        if (!clicked) {
            return;
        }

        final boolean moduleState = module.getModuleState();
        moduleToggleMap.put(module, !moduleState);
    }

    private void renderSettings(final SettingsWidget settingsWidget) {
        settingsWidget.preProcess();
        settingsWidget.process();
        settingsWidget.postProcess();
    }

    private void renderCategory(final AtomicInteger offsetAtomic, final String name, final Set<Module> modules) {
        ImGui.begin(name);

        modules.forEach(this::renderModule);

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
