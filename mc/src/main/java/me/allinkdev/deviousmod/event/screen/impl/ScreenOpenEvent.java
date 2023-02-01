package me.allinkdev.deviousmod.event.screen.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.allinkdev.deviousmod.event.Event;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class ScreenOpenEvent extends Event {
    private final ScreenOpenTarget target;

    public static Optional<Screen> getOverriddenScreen(final Screen screen) {
        final ScreenOpenTarget target = new ScreenOpenTarget(screen);
        final ScreenOpenEvent event = new ScreenOpenEvent(target);

        eventBus.post(event);

        return target.getScreen();
    }

    @AllArgsConstructor
    public static class ScreenOpenTarget {
        @Nullable
        private Screen target;

        public Optional<Screen> getScreen() {
            return Optional.ofNullable(this.target);
        }

        public void setScreen(final Screen newScreen) {
            this.target = newScreen;
        }
    }
}
