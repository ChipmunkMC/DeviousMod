package me.allinkdev.deviousmod.mixin.client.screen;

import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatScreen.class)
public final class InfiniChat {
    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setMaxLength(I)V"))
    private void onSetMaxLength(final TextFieldWidget instance, final int maxLength) {
        instance.setMaxLength(Integer.MAX_VALUE);
    }
}
