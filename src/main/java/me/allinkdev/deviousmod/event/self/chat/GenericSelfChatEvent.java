package me.allinkdev.deviousmod.event.self.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.allinkdev.deviousmod.event.Cancellable;

@Getter
@Setter
@AllArgsConstructor
public class GenericSelfChatEvent extends Cancellable {
    private final String message;
}
