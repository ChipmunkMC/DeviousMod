package me.allinkdev.deviousmod.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class IteratorUtil {
    public static <T> List<T> toList(final Iterator<T> iterator) {
        final List<T> list = new ArrayList<>();

        iterator.forEachRemaining(list::add);
        return Collections.unmodifiableList(list);
    }
}
