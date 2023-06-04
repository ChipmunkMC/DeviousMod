package me.allinkdev.deviousmod.event.self.chat;

import com.github.allinkdev.deviousmod.api.event.Cancellable;

public class GenericSelfChatEvent extends Cancellable {
    private final String message;

    protected GenericSelfChatEvent(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
