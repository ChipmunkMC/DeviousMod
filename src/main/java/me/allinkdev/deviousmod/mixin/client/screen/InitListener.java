package me.allinkdev.deviousmod.mixin.client.screen;

import me.allinkdev.deviousmod.event.screen.impl.InitScreenEvent;
import me.allinkdev.deviousmod.mixin.accessor.ScreenAccessor;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public final class InitListener {
    private void handleInit() {
        final Screen thiz = (Screen) (Object) this;
        final ScreenAccessor accessor = (ScreenAccessor) thiz;
        EventUtil.postEvent(new InitScreenEvent(thiz)).getConsumers().forEach(screenConsumer -> screenConsumer.accept(thiz, accessor));
    }

    @Inject(method = "clearAndInit", at = @At(value = "RETURN", target = "Lnet/minecraft/client/gui/screen/Screen;init()V"))
    private void onInit(final CallbackInfo ci) {
        this.handleInit();
    }

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At(value = "RETURN", target = "Lnet/minecraft/client/gui/screen/Screen;init()V"))
    private void onFinalInit(final MinecraftClient client, final int width, final int height, final CallbackInfo ci) {
        this.handleInit();
    }
}
