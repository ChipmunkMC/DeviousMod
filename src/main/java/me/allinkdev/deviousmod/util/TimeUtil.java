package me.allinkdev.deviousmod.util;

public final class TimeUtil extends NoConstructor {
    public static long getInTicks(final long ms) {
        return ms / 20L;
    }
}
