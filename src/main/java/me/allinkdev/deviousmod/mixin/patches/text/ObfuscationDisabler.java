package me.allinkdev.deviousmod.mixin.patches.text;

import net.minecraft.text.Style;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Style.class)
public final class ObfuscationDisabler {
    @Mutable
    @Shadow
    @Final
    @Nullable Boolean obfuscated;

    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/text/Style;obfuscated:Ljava/lang/Boolean;", opcode = Opcodes.PUTFIELD))
    private void onPutObfuscated(final Style instance, final Boolean value) {
        this.obfuscated = false;
    }
}
