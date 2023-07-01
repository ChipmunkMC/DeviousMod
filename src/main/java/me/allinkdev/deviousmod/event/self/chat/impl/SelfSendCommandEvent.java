package me.allinkdev.deviousmod.event.self.chat.impl;

import me.allinkdev.deviousmod.event.self.chat.GenericSelfChatEvent;

public final class SelfSendCommandEvent extends GenericSelfChatEvent {
    private final boolean wasQueued;

    public SelfSendCommandEvent(final String message, final boolean wasQueued) {
        super(message);
        this.wasQueued = wasQueued;
    }

    public boolean wasQueued() {
        return this.wasQueued;
    }
}
