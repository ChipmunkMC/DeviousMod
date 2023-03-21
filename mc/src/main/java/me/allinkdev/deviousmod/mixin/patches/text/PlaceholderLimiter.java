package me.allinkdev.deviousmod.mixin.patches.text;

import net.minecraft.text.StringVisitable;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(TranslatableTextContent.class)
public final class PlaceholderLimiter {
    @Shadow
    @Final
    private static Pattern ARG_FORMAT;

    @Inject(method = "forEachPart", at = @At("HEAD"), cancellable = true)
    public void onForEachPart(final String translation, final Consumer<StringVisitable> partsConsumer, final CallbackInfo ci) {
        final Matcher matcher = ARG_FORMAT.matcher(translation);
        final int count = matcher.groupCount();

        if (count < 10) {
            return;
        }

        ci.cancel();
    }
}
