package me.allinkdev.deviousmod.event.screen.impl;

import com.github.allinkdev.deviousmod.api.event.Event;
import me.allinkdev.deviousmod.mixin.accessor.ScreenAccessor;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public class InitScreenEvent implements Event {
    private final Set<BiConsumer<Screen, ScreenAccessor>> screenConsumers = Collections.synchronizedSet(new LinkedHashSet<>());
    private final Screen screen;

    public InitScreenEvent(final Screen screen) {
        this.screen = screen;
    }

    public static void onAfterInit(final MinecraftClient client, final Screen screen, final int scaledWidth, final int scaledHeight) {
        final ScreenAccessor accessor = (ScreenAccessor) screen;
        EventUtil.postEvent(new InitScreenEvent(screen)).getConsumers().forEach(c -> c.accept(screen, accessor));
    }

    public Screen getScreen() {
        return this.screen;
    }

    public void addConsumer(final BiConsumer<Screen, ScreenAccessor> screenConsumer) {
        this.screenConsumers.add(screenConsumer);
    }

    public @Unmodifiable Set<BiConsumer<Screen, ScreenAccessor>> getConsumers() {
        return Collections.unmodifiableSet(screenConsumers);
    }
}
