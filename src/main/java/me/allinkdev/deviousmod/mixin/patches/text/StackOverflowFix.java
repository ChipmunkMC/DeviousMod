package me.allinkdev.deviousmod.mixin.patches.text;

import com.google.gson.*;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(Text.Serializer.class)
public final class StackOverflowFix {
    @Unique
    private static final Text BLOCK_MESSAGE = Text.literal("**STACK OVERFLOW**").setStyle(Style.EMPTY.withItalic(false).withFormatting(Formatting.RED));

    // I defeated the recursion with recursion
    @Unique
    private boolean isOver(final JsonElement element, final AtomicInteger totalDepth) {
        if (element instanceof JsonPrimitive) return false;
        if (totalDepth.get() > 127) return true;
        if (element instanceof final JsonArray jsonArray) {
            totalDepth.incrementAndGet();
            for (final JsonElement jsonElement : jsonArray) if (isOver(jsonElement, totalDepth)) return true;
        } else if (element instanceof final JsonObject jsonObject) {
            final JsonElement with;
            final JsonElement extra;
            if (jsonObject.has("text") && (extra = jsonObject.get("extra")) instanceof final JsonArray extraArr) {
                totalDepth.incrementAndGet();
                for (final JsonElement jsonElement : extraArr) {
                    if (isOver(jsonElement, totalDepth)) return true;
                }
            } else if (jsonObject.has("translate") && (with = jsonObject.get("with")) instanceof final JsonArray withArr) {
                totalDepth.incrementAndGet();
                for (final JsonElement jsonElement : withArr) {
                    if (isOver(jsonElement, totalDepth)) return true;
                }
            }
        }
        return false;
    }

    @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/text/MutableText;", at = @At("HEAD"), cancellable = true)
    private void deserialize(final JsonElement jsonElement, final Type type, final JsonDeserializationContext jsonDeserializationContext, final CallbackInfoReturnable<MutableText> cir) {
        if (jsonElement instanceof JsonPrimitive) return;
        if (isOver(jsonElement, new AtomicInteger())) cir.setReturnValue(BLOCK_MESSAGE.copy());
    }
}
