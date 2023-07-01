package me.allinkdev.deviousmod.event.chat;

import com.github.allinkdev.deviousmod.api.event.Event;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class ChatEvent implements Event {
    private final Text message;
    private final @Nullable UUID sender;
    private final Type type;

    public ChatEvent(final Text message, final @Nullable UUID sender, final Type type) {
        this.message = message;
        this.sender = sender;
        this.type = type;
    }

    public Text getMessage() {
        return this.message.copy();
    }

    public String getContent() {
        return this.message.getString();
    }

    public Optional<UUID> getSender() {
        return Optional.ofNullable(this.sender);
    }

    public Type getType() {
        return this.type;
    }

    public enum Type {
        PLAYER,
        DISGUISED,
        SYSTEM
    }
}
