package me.allinkdev.deviousmod.gui;

import me.allinkdev.deviousmod.DeviousMod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractImGuiLayer {
    protected final DeviousMod deviousMod;
    private final List<AbstractImGuiLayer> children = Collections.synchronizedList(new ArrayList<>());

    protected AbstractImGuiLayer() {
        this.deviousMod = null;
    }

    protected AbstractImGuiLayer(final DeviousMod deviousMod) {
        this.deviousMod = deviousMod;
    }

    protected void addChild(final AbstractImGuiLayer imGuiLayer) {
        this.children.add(imGuiLayer);
    }

    public List<AbstractImGuiLayer> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void preProcess() {

    }

    public void init() {
        
    }

    public abstract void process();

    public void postProcess() {

    }
}
