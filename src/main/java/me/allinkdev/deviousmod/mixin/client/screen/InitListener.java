package me.allinkdev.deviousmod.mixin.client.screen;

import com.github.allinkdev.deviousmod.api.managers.EventManager;
import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.screen.impl.InitScreenEvent;
import me.allinkdev.deviousmod.mixin.accessor.ScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public final class InitListener {
    private void handleInit() {
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventManager<EventBus> eventManager = deviousMod.getEventManager();
        final Screen thiz = (Screen) (Object) this;
        final ScreenAccessor accessor = (ScreenAccessor) thiz;
        final InitScreenEvent event = new InitScreenEvent(thiz);
        eventManager.broadcastEvent(event);
        event.getConsumers().forEach(screenConsumer -> screenConsumer.accept(thiz, accessor));
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
