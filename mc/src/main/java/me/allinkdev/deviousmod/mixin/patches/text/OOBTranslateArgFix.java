package me.allinkdev.deviousmod.mixin.patches.text;

import me.allinkdev.deviousmod.util.TextUtil;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TranslatableTextContent.class)
public class OOBTranslateArgFix {
    @Inject(method = "getArg", at = @At("HEAD"), cancellable = true)
    public void onGetArg(final int index, final CallbackInfoReturnable<StringVisitable> cir) {
        if (index < 0) {
            cir.setReturnValue(TextUtil.INVALID_JSON);
        }
    }
}