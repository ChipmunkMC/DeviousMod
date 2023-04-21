package me.allinkdev.deviousmod.event.self.chat.impl;

import me.allinkdev.deviousmod.event.self.chat.GenericSelfChatEvent;

public final class SelfSendCommandEvent extends GenericSelfChatEvent {
    public SelfSendCommandEvent(final String message) {
        super(message);
    }
}
