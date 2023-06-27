package me.allinkdev.deviousmod.event.render.entity;

import com.github.allinkdev.deviousmod.api.event.Cancellable;
import net.minecraft.client.render.RenderLayer;

public final class RenderLayerEvent extends Cancellable {
    private final RenderLayer renderLayer;

    public RenderLayerEvent(final RenderLayer renderLayer) {
        this.renderLayer = renderLayer;
    }

    public RenderLayer getRenderLayer() {
        return this.renderLayer;
    }
}
