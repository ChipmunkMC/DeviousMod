package me.allinkdev.deviousmod.event.title.panorama;

import com.github.allinkdev.deviousmod.api.event.Event;
import net.minecraft.util.Identifier;

public final class PanoramaDirectorySelectEvent implements Event {
    private Identifier textureDirectory;

    public PanoramaDirectorySelectEvent(final Identifier textureDirectory) {
        this.textureDirectory = textureDirectory;
    }

    public Identifier getTextureDirectory() {
        return this.textureDirectory;
    }

    public void setTextureDirectory(final Identifier textureDirectory) {
        this.textureDirectory = textureDirectory;
    }
}
