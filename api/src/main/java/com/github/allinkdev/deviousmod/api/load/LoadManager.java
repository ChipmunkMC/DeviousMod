package com.github.allinkdev.deviousmod.api.load;

import com.github.allinkdev.deviousmod.api.DeviousModSilhouette;

import java.util.HashSet;
import java.util.Set;

/**
 * Class that allows for registering of {@link LoadListener} objects.
 */
public final class LoadManager {
    private static final Set<LoadListener> listeners = new HashSet<>();
    private static boolean loaded = false;

    private LoadManager() {
        //
    }

    /**
     * Registers a load listener. This will be called immediately if Devious Mod has already loaded, so please make sure to initialize anything that the load listener may depend on BEFORE you register it!
     *
     * @param loadListener the load listener to register
     */
    public static void registerLoadListener(final LoadListener loadListener) {
        listeners.add(loadListener);

        if (!loaded) {
            return;
        }

        final DeviousModSilhouette instance = DeviousModSilhouette.getInstance();
        loadListener.onLoad(instance);
    }

    /**
     * Unregisters the load listener. I'm not particularly sure why you would want to do this, but it's here if you want it.
     *
     * @param loadListener the load listener to unregister
     */
    public static void unregisterLoadListener(final LoadListener loadListener) {
        listeners.remove(loadListener);
    }

    /**
     * Marks Devious Mod as loaded and broadcasts load to every load listener with the provided {@link DeviousModSilhouette} object.
     *
     * @param silhouette the provided {@link DeviousModSilhouette}
     */
    public static void load(final DeviousModSilhouette silhouette) {
        if (loaded) {
            throw new IllegalStateException("Already loaded!");
        }

        DeviousModSilhouette.setInstance(silhouette);
        loaded = true;

        listeners.forEach(l -> l.onLoad(silhouette));
    }
}
