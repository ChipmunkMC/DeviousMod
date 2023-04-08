package me.allinkdev.deviousmod.mixin.patches.text;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PacketByteBuf.class)
public final class NullComponentFallback {
    private static final MutableText DEFAULT_COMPONENT = Text.literal("NULL!!!")
            .formatted(Formatting.RED);

    @Redirect(method = "readText", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text$Serializer;fromJson(Ljava/lang/String;)Lnet/minecraft/text/MutableText;"))
    private MutableText onFromJson(final String json) {
        final MutableText deserializedText = Text.Serializer.fromJson(json);

        return deserializedText == null ? DEFAULT_COMPONENT : deserializedText;
    }
}
