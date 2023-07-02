package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.event.title.panorama.PanoramaDirectorySelectEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import net.minecraft.util.Identifier;

import java.security.SecureRandom;

public final class KaboomPanoramasModule extends DModule {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String[] DIRECTORIES = new String[]{"kaboom", "kitsune", "mcslot", "raccoon"};

    public KaboomPanoramasModule(final DModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    public String getModuleName() {
        return "KaboomPanoramas";
    }

    @Override
    public String getDescription() {
        return "Replaces the vanilla panoramas with those of Kaboom clones.";
    }

    @Override
    public String getCategory() {
        return "Kaboom";
    }

    @Subscribe
    private void onPanoramaSelect(final PanoramaDirectorySelectEvent event) {
        event.setTextureDirectory(new Identifier("deviousmod", "panoramas/" + DIRECTORIES[SECURE_RANDOM.nextInt(DIRECTORIES.length)] + "/panorama"));
    }
}
