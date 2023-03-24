package me.allinkdev.deviousmod.module.impl;

import com.google.common.util.concurrent.AtomicDouble;
import me.allinkdev.deviousmod.event.transformer.impl.InGameUITransformer;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.ModuleManager;
import net.minecraft.SharedConstants;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public final class LogosModule extends DModule implements InGameUITransformer {
    private static final String GAME_VERSION = SharedConstants.getGameVersion().getName();
    private static final String TEXT = "DeviousMod " + GAME_VERSION;
    private static final AtomicDouble at = new AtomicDouble(0);
    private static final int WHITE = ColorHelper.Argb.getArgb(255, 255, 255, 255);
    private static final int BLACK = ColorHelper.Argb.getArgb(255, 0, 0, 0);

    public LogosModule(final ModuleManager moduleManager) {
        super(moduleManager);
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
    public void init() {
        addTransformer(this);
    }

    @Override
    public void render(final MatrixStack matrices, final float tickDelta, final CallbackInfo ci) {
        if (client.options.debugEnabled) {
            return;
        }

        client.inGameHud.setCanShowChatDisabledScreen(true);

        final double w = at.addAndGet(.05);
        final double x = Math.sin(w) * 1.1;
        final double y = Math.cos(w) * 1.1;

        final TextRenderer textRenderer = client.textRenderer;
        final MatrixStack shadowStack = new MatrixStack();
        shadowStack.translate(x, y, 0);

        textRenderer.draw(shadowStack, TEXT, 4, 4, BLACK);
        textRenderer.draw(matrices, TEXT, 4, 4, WHITE);
    }
}
