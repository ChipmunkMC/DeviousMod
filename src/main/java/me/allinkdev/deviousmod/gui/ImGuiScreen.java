package me.allinkdev.deviousmod.gui;

import me.allinkdev.deviousmod.DeviousMod;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class ImGuiScreen extends Screen {
    private final AbstractImGuiLayer imGuiLayer;

    public ImGuiScreen(final Text text, final AbstractImGuiLayer imGuiLayer) {
        super(text);
        this.imGuiLayer = imGuiLayer;
        this.imGuiLayer.layerInit();
        DeviousMod.getInstance().getImGuiHolder().addLayer(this.imGuiLayer);
    }

    public ImGuiScreen(final AbstractImGuiLayer imGuiLayer) {
        this(ScreenTexts.EMPTY, imGuiLayer);
    }

    public static ImGuiScreen from(final AbstractImGuiLayer abstractImGuiLayer) {
        return new ImGuiScreen(abstractImGuiLayer);
    }

    @Override
    public void render(final DrawContext drawContext, final int mouseX, final int mouseY, final float tickDelta) {
        this.renderBackground(drawContext);
    }

    @Override
    public void removed() {
        DeviousMod.getInstance().getImGuiHolder().removeLayer(this.imGuiLayer);
    }
}
