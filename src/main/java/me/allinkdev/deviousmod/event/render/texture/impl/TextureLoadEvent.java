package me.allinkdev.deviousmod.event.render.texture.impl;

import me.allinkdev.deviousmod.event.render.texture.GenericTextureEvent;
import net.minecraft.client.texture.AbstractTexture;

public final class TextureLoadEvent extends GenericTextureEvent {
    public TextureLoadEvent(final AbstractTexture texture) {
        super(texture);
    }
}
