package com.github.allinkdev.deviousmod.api.experiments;

/**
 * Contains data on how experimental a class is.
 */
public enum Experimentality {
    /**
     * The class is not experimental at all.
     */
    NONE,
    /**
     * The class is slightly experimental, but more or less ready for production use.
     */
    WARN,
    /**
     * The class is highly experimental, and is hidden in production clients.
     */
    HIDE,
}
