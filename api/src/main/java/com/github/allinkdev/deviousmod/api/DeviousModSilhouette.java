package com.github.allinkdev.deviousmod.api;

import com.github.allinkdev.deviousmod.api.managers.CommandManager;
import com.github.allinkdev.deviousmod.api.managers.ModuleManager;

public interface DeviousModSilhouette {
    static DeviousModSilhouette getInstance() {
        return Store.getInstance();
    }

    static void setInstance(final DeviousModSilhouette silhouette) {
        Store.setInstance(silhouette);
    }

    ModuleManager getModuleManager();

    <T> CommandManager<T> getCommandManager();
    
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
