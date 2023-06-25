package me.allinkdev.deviousmod.mixin.accessor;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Screen.class)
public interface ScreenAccessor {
    @Invoker
    <T extends Drawable> T invokeAddDrawable(T drawable);

    @Invoker
    <T extends Element & Selectable> T invokeAddSelectableChild(T child);

    @Invoker
    <T extends Element & Drawable & Selectable> T invokeAddDrawableChild(T drawableElement);
}
