package me.allinkdev.deviousmod.util;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.util.Set;

public final class BukkitUtil extends NoConstructor {
    // I LOVE BUKKIT! I LOVE BUKKIT! I LOVE BUKKIT!
    public static boolean isSameCommand(final String input, final String prefix, final String... aliases) {
        final Set<String> possibilities = new ObjectArraySet<>();

        for (final String alias : aliases) {
            possibilities.add(alias);
            possibilities.add(prefix + ":" + alias);
        }

        return possibilities.contains(input);
    }
}
