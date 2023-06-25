package me.allinkdev.deviousmod.util;

import net.kyori.adventure.platform.fabric.FabricClientAudiences;
import net.kyori.adventure.text.Component;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public final class TextUtil extends NoConstructor {
    public static final MutableText EMPTY_TEXT = Text.empty();
    public static final MutableText INVALID_JSON = MutableText.of(new LiteralTextContent("Invalid JSON!"));
    private static final FabricClientAudiences AUDIENCE = FabricClientAudiences.of();

    public static Text toText(final Component component) {
        return AUDIENCE.toNative(component);
    }

    public static void sendMessage(final Component component) {
        AUDIENCE.audience().sendMessage(component);
    }

    public static @Nullable Component toAdventureNullable(final Text vanilla) {
        return vanilla == null ? null : vanilla.asComponent();
    }
}
