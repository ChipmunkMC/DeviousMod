package me.allinkdev.deviousmod.event.window;

import com.github.allinkdev.deviousmod.api.event.Event;

public final class WindowFocusChangeEvent implements Event {
    private final boolean oldFocus;
    private final boolean newFocus;

    public WindowFocusChangeEvent(final boolean oldFocus, final boolean newFocus) {
        this.oldFocus = oldFocus;
        this.newFocus = newFocus;
    }

    public boolean getOldFocus() {
        return this.oldFocus;
    }

    public boolean getNewFocus() {
        return this.newFocus;
    }
}
