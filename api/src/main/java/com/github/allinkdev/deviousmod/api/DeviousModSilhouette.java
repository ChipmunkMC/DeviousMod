package com.github.allinkdev.deviousmod.api;

import com.github.allinkdev.deviousmod.api.managers.CommandManager;
import com.github.allinkdev.deviousmod.api.managers.ModuleManager;

/**
 * Class that contains a {@link ModuleManager} instance and a {@link CommandManager} instance.
 */
public interface DeviousModSilhouette {
    /**
     * @return the current implementation of {@link DeviousModSilhouette}
     */
    static DeviousModSilhouette getInstance() {
        return Store.getInstance();
    }

    /**
     * Set the current implementation of {@link DeviousModSilhouette}.
     *
     * @param silhouette the {@link DeviousModSilhouette} implementation
     */
    static void setInstance(final DeviousModSilhouette silhouette) {
        Store.setInstance(silhouette);
    }

    /**
     * @return the module manager instance associated with this {@link DeviousModSilhouette}
     */
    ModuleManager getModuleManager();

    /**
     * @param <T> the command node type
     * @return the command manager associated with this {@link DeviousModSilhouette}
     */

    <T> CommandManager<T> getCommandManager();

    /**
     * Class that stores the current instance of the {@link DeviousModSilhouette} implementation. This is for internal usage only.
     */
    final class Store {
        private static DeviousModSilhouette INSTANCE;

        private Store() {

        }

        private static DeviousModSilhouette getInstance() {
            return INSTANCE;
        }

        private static void setInstance(final DeviousModSilhouette silhouette) {
            if (INSTANCE != null) {
                throw new IllegalStateException("Tried to set DeviousMod silhouette when it was already set!");
            }

            INSTANCE = silhouette;
        }
    }
}
