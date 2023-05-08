package com.github.allinkdev.deviousmod.api.load;

import com.github.allinkdev.deviousmod.api.DeviousModSilhouette;
import com.github.allinkdev.deviousmod.api.managers.CommandManager;
import com.github.allinkdev.deviousmod.api.managers.ModuleManager;

/**
 * Listener that makes it possible for dependent mods to execute logic immediately after the {@link DeviousModSilhouette} implementation loads.
 * <p>
 * Register implementations of this listener with {@link LoadManager}.
 */
public interface LoadListener {
    /**
     * Called when the current {@link DeviousModSilhouette} implementation is fully loaded. The {@link ModuleManager} object and {@link CommandManager} object will also be fully loaded at this stage, as well.
     *
     * @param deviousMod the {@link DeviousModSilhouette} implementation
     */
    void onLoad(final DeviousModSilhouette deviousMod);
}
