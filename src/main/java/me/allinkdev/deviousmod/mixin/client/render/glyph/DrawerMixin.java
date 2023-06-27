package me.allinkdev.deviousmod.mixin.client.render.glyph;

import me.allinkdev.deviousmod.event.render.text.ObfuscatedGlyphRendererSelectEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net/minecraft/client/font/TextRenderer$Drawer")
public final class DrawerMixin {
    @Redirect(method = "accept", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Style;isObfuscated()Z"))
    private boolean onIsObfuscated(final Style instance) {
        return instance.isObfuscated() && !EventUtil.postCancellable(new ObfuscatedGlyphRendererSelectEvent());
    }
}