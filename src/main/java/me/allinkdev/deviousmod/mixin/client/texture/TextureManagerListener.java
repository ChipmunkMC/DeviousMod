package me.allinkdev.deviousmod.mixin.client.texture;

import me.allinkdev.deviousmod.event.render.texture.impl.TextureLoadEvent;
import me.allinkdev.deviousmod.event.render.texture.impl.TextureUnloadEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TextureManager.class)
public final class TextureManagerListener {
    @Inject(method = "registerTexture", at = @At("HEAD"))
    private void onRegisterTexture(final Identifier id, final AbstractTexture texture, final CallbackInfo ci) {
        EventUtil.postEvent(new TextureLoadEvent(texture));
    }

    @Inject(method = "closeTexture", at = @At("HEAD"))
    private void onDestroyTexture(final Identifier id, final AbstractTexture texture, final CallbackInfo ci) {
        EventUtil.postEvent(new TextureUnloadEvent(texture));
    }
}
