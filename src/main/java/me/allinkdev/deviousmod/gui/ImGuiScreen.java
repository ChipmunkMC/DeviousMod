package me.allinkdev.deviousmod.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ImGuiScreen extends Screen {
    private final AbstractImGuiLayer imGuiLayer;

    public ImGuiScreen(final Text text, final AbstractImGuiLayer imGuiLayer) {
        super(text);
        this.imGuiLayer = imGuiLayer;
        this.imGuiLayer.layerInit();
        ImGuiHolder.addLayer(this.imGuiLayer);
    }

    public ImGuiScreen(final AbstractImGuiLayer imGuiLayer) {
        this(Text.of("fart"), imGuiLayer);
    }

    public static ImGuiScreen from(final AbstractImGuiLayer abstractImGuiLayer) {
        return new ImGuiScreen(abstractImGuiLayer);
    }

    @Override
    public void render(final MatrixStack matrixStack, final int mouseX, final int mouseY, final float tickDelta) {
        this.renderBackground(matrixStack);
    }

    @Override
    public void removed() {
        ImGuiHolder.removeLayer(this.imGuiLayer);
    }
}
