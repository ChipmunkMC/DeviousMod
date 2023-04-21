package me.allinkdev.deviousmod.event.self.chat.impl;

import me.allinkdev.deviousmod.event.self.chat.GenericSelfChatEvent;

public final class SelfSendMessageEvent extends GenericSelfChatEvent {
    public SelfSendMessageEvent(final String message) {
        super(message);
    }
}
