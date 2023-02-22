package me.allinkdev.deviousmod.mixin.client.screen;

import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.transformer.impl.InGameUITransformer;
import me.allinkdev.deviousmod.module.ModuleManager;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(InGameHud.class)
public class HudTransformer {
    private List<InGameUITransformer> getTransformers() {
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final ModuleManager moduleManager = deviousMod.getModuleManager();

        return moduleManager.getTransformers(InGameUITransformer.class);
    }

    @Inject(method = "render", at = @At(value = "HEAD"), cancellable = true)
    public void onRender(final MatrixStack matrices, final float tickDelta, final CallbackInfo ci) {
        final List<InGameUITransformer> transformers = getTransformers();

        for (final InGameUITransformer transformer : transformers) {
            transformer.preRender(matrices, tickDelta, ci);
            transformer.render(matrices, tickDelta, ci);
        }
    }

    @Inject(method = "render", at = @At(value = "RETURN"))
    public void onPostRender(final MatrixStack matrices, final float tickDelta, final CallbackInfo ci) {
        final List<InGameUITransformer> transformers = getTransformers();

        for (final InGameUITransformer transformer : transformers) {
            transformer.postRender(matrices, tickDelta);
        }
    }
}
