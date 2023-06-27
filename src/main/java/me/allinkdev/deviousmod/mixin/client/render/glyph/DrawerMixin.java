package me.allinkdev.deviousmod.mixin.client.render.glyph;

import com.github.allinkdev.deviousmod.api.managers.EventManager;
import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.render.text.ObfuscatedGlyphRendererSelectEvent;
import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "net/minecraft/client/font/TextRenderer$Drawer")
public final class DrawerMixin {
    @Redirect(method = "accept", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Style;isObfuscated()Z"))
    private boolean onIsObfuscated(final Style instance) {
        if (!instance.isObfuscated()) {
            return false;
        }

        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventManager<EventBus> eventManager = deviousMod.getEventManager();
        final ObfuscatedGlyphRendererSelectEvent event = new ObfuscatedGlyphRendererSelectEvent();
        eventManager.broadcastEvent(event);
        return !event.isCancelled();
    }
}