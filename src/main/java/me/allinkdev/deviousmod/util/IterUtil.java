package me.allinkdev.deviousmod.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class IterUtil {
    public static <T> List<T> toList(final Iterator<T> iterator) {
        final List<T> list = new ArrayList<>();

        iterator.forEachRemaining(list::add);
        return list;
    }

    public static <T> List<T> toList(final Iterable<T> iterable) {
        final List<T> list = new ArrayList<>();

        iterable.forEach(list::add);
        return list;
    }
}
