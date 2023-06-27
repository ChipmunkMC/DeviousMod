package me.allinkdev.deviousmod.render;

import com.github.allinkdev.deviousmod.api.managers.EventManager;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.render.texture.impl.TextureLoadEvent;
import me.allinkdev.deviousmod.event.render.texture.impl.TextureUnloadEvent;
import net.minecraft.client.texture.AbstractTexture;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class RenderManager {
    private final Map<Integer, AbstractTexture> textureMap = Collections.synchronizedMap(new HashMap<>());

    public RenderManager(final DeviousMod deviousMod) {
        final EventManager<EventBus> eventManager = deviousMod.getEventManager();
        eventManager.registerListener(this);
    }

    public Optional<AbstractTexture> searchForTexture(final int glId) {
        return Optional.ofNullable(this.textureMap.get(glId));
    }

    @Subscribe
    public void onTextureLoad(final TextureLoadEvent event) {
        final AbstractTexture loadedTexture = event.getTexture();

        this.textureMap.put(loadedTexture.getGlId(), loadedTexture);
    }

    @Subscribe
    public void onTextureUnload(final TextureUnloadEvent event) {
        final AbstractTexture unloadedTexture = event.getTexture();

        this.textureMap.remove(unloadedTexture.getGlId());
    }
}
