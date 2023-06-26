package me.allinkdev.deviousmod.util;

import org.jetbrains.annotations.Nullable;

public class ObjectUtil {
    public static boolean nullSafeEquals(final @Nullable Object o1, final @Nullable Object o2) {
        return (o1 != null && o2 != null) && o1.equals(o2);
    }
}
