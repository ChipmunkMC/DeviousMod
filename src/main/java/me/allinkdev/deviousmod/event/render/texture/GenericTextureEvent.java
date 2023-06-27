package me.allinkdev.deviousmod.event.render.texture;

import com.github.allinkdev.deviousmod.api.event.Event;
import net.minecraft.client.texture.AbstractTexture;

public abstract class GenericTextureEvent implements Event {
    private final AbstractTexture texture;

    public GenericTextureEvent(final AbstractTexture texture) {
        this.texture = texture;
    }

    public AbstractTexture getTexture() {
        return this.texture;
    }
}
