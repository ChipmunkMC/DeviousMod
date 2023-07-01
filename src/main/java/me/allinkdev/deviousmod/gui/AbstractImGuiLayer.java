package me.allinkdev.deviousmod.gui;

import com.github.allinkdev.deviousmod.api.gui.ImGuiLayer;
import me.allinkdev.deviousmod.DeviousMod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractImGuiLayer implements ImGuiLayer {
    protected final DeviousMod deviousMod;
    private final List<ImGuiLayer> children = Collections.synchronizedList(new ArrayList<>());

    protected AbstractImGuiLayer() {
        this.deviousMod = null;
    }

    protected AbstractImGuiLayer(final DeviousMod deviousMod) {
        this.deviousMod = deviousMod;
    }

    @Override
    public void addChild(final ImGuiLayer imGuiLayer) {
        this.children.add(imGuiLayer);
    }

    @Override
    public List<ImGuiLayer> getChildren() {
        return Collections.unmodifiableList(children);
    }
}
