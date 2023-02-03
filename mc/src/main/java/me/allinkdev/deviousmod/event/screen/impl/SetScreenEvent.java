package me.allinkdev.deviousmod.event.screen.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.allinkdev.deviousmod.event.Event;
import net.minecraft.client.gui.screen.Screen;

@Getter
@AllArgsConstructor
public class SetScreenEvent extends Event {
    @Setter
    private Screen target;
}
