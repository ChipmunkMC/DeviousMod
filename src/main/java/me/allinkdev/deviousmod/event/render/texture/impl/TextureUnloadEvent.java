package me.allinkdev.deviousmod.event.render.texture.impl;

import me.allinkdev.deviousmod.event.render.texture.GenericTextureEvent;
import net.minecraft.client.texture.AbstractTexture;

public final class TextureUnloadEvent extends GenericTextureEvent {
    public TextureUnloadEvent(final AbstractTexture texture) {
        super(texture);
    }
}
