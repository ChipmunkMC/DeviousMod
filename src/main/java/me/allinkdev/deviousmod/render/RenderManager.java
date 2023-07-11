package me.allinkdev.deviousmod.render;

import me.allinkdev.deviousmod.event.render.texture.impl.TextureLoadEvent;
import me.allinkdev.deviousmod.event.render.texture.impl.TextureUnloadEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.lenni0451.lambdaevents.EventHandler;
import net.minecraft.client.texture.AbstractTexture;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class RenderManager {
    private final Map<Integer, AbstractTexture> textureMap = Collections.synchronizedMap(new HashMap<>());

    public RenderManager() {
        EventUtil.registerListener(this);
    }

    public Optional<AbstractTexture> searchForTexture(final int glId) {
        return Optional.ofNullable(this.textureMap.get(glId));
    }

    @EventHandler
    public void onTextureLoad(final TextureLoadEvent event) {
        final AbstractTexture loadedTexture = event.getTexture();

        this.textureMap.put(loadedTexture.getGlId(), loadedTexture);
    }

    @EventHandler
    public void onTextureUnload(final TextureUnloadEvent event) {
        final AbstractTexture unloadedTexture = event.getTexture();

        this.textureMap.remove(unloadedTexture.getGlId());
    }
}
