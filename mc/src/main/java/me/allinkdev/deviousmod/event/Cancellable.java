package me.allinkdev.deviousmod.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class Cancellable extends Event {
    private boolean cancelled = false;
}
