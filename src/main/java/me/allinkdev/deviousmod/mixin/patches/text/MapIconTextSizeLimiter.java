package me.allinkdev.deviousmod.mixin.patches.text;

import me.allinkdev.deviousmod.util.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.item.map.MapIcon;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MapIcon.class)
public final class MapIconTextSizeLimiter {
    private static final Text LAG_ICON_TEXT = TextUtil.toText(Component.text("**LAG ATTEMPT BLOCKED**", NamedTextColor.RED));

    @Mutable
    @Shadow
    @Final
    private @Nullable Text text;

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/item/map/MapIcon;text:Lnet/minecraft/text/Text;", opcode = Opcodes.PUTFIELD))
    private void onInit(final MapIcon instance, final Text value) {
        if (value == null) return;
        final String content = value.getString();
        if (content.length() < 30) return;
        text = LAG_ICON_TEXT;
    }
}
