package me.allinkdev.deviousmod.mixin.client.texture;

import com.github.allinkdev.deviousmod.api.event.Event;
import com.github.allinkdev.deviousmod.api.managers.EventManager;
import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.render.texture.impl.TextureLoadEvent;
import me.allinkdev.deviousmod.event.render.texture.impl.TextureUnloadEvent;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureManager.class)
public class TextureManagerListener {
    private void callEvent(final Event event) {
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventManager<EventBus> eventManager = deviousMod.getEventManager();
        eventManager.broadcastEvent(event);
    }

    @Inject(method = "registerTexture", at = @At("HEAD"))
    private void onRegisterTexture(final Identifier id, final AbstractTexture texture, final CallbackInfo ci) {
        this.callEvent(new TextureLoadEvent(texture));
    }

    @Inject(method = "closeTexture", at = @At("HEAD"))
    private void onDestroyTexture(final Identifier id, final AbstractTexture texture, final CallbackInfo ci) {
        this.callEvent(new TextureUnloadEvent(texture));
    }
}
