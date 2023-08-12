package me.allinkdev.deviousmod.gui;

import com.github.allinkdev.deviousmod.api.gui.ImGuiHolder;
import com.github.allinkdev.deviousmod.api.gui.ImGuiLayer;

import java.util.LinkedHashSet;
import java.util.Set;

public class ImGuiHolderProxy implements ImGuiHolder {
    private final Set<ImGuiLayer> layerQueue = new LinkedHashSet<>();
    private ImGuiHolder actualHolder;

    public void setActualHolder(final ImGuiHolder actualHolder) {
        if (this.actualHolder != null) throw new IllegalStateException("Tried to overwrite an already present proxied holder!");
        this.actualHolder = actualHolder;
        this.layerQueue.forEach(this.actualHolder::addLayer);
        this.layerQueue.clear();
    }

    @Override
    public void process(final ImGuiLayer layer) {
        if (this.actualHolder != null) {
            this.actualHolder.process(layer);
            return;
        }

        this.layerQueue.add(layer);
    }

    @Override
    public void render() {
        if (this.actualHolder == null) return;
        this.actualHolder.render();
    }

    @Override
    public void addLayer(final ImGuiLayer layer) {
        if (this.actualHolder != null) {
            this.actualHolder.addLayer(layer);
            return;
        }

        this.layerQueue.add(layer);
    }

    @Override
    public void removeLayer(final ImGuiLayer layer) {
        if (this.actualHolder != null) {
            this.actualHolder.removeLayer(layer);
            return;
        }

        this.layerQueue.remove(layer);
    }

    @Override
    public void newFrame() {
        if (this.actualHolder == null) return;
        this.actualHolder.newFrame();
    }

    @Override
    public void handleScrollCallback(final long id, final double xO, final double yO) {
        if (this.actualHolder == null) return;
        this.actualHolder.handleScrollCallback(id, xO, yO);
    }
}
