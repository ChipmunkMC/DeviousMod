package me.allinkdev.deviousmod.util;

import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.kyori.adventure.text.Component;
import net.minecraft.text.Text;

public class TextUtil {
    private static final FabricClientAudiences audience = FabricClientAudiences.of();

    public static Text toText(final Component component) {
        return audience.toNative(component);
    }
}