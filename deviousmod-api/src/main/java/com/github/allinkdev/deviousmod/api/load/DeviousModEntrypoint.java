package com.github.allinkdev.deviousmod.api.load;

import com.github.allinkdev.deviousmod.api.DeviousModSilhouette;

/**
 * What a {@link DeviousModSilhouette} implementation will call when they are fully loaded.
 */
public interface DeviousModEntrypoint {
    /**
     * Called when the current {@link DeviousModSilhouette} implementation is fully loaded.
     *
     * @param deviousMod the {@link DeviousModSilhouette} implementation
     */
    void onLoad(final DeviousModSilhouette deviousMod);

    /**
     * Called before the current {@link DeviousModSilhouette} implementation has fully loaded.
     *
     * @param deviousMod the {@link DeviousModSilhouette} implementation
     */
    default void onPreLoad(final DeviousModSilhouette deviousMod) {
        //
    }
}
