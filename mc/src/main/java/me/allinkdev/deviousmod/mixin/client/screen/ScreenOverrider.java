package me.allinkdev.deviousmod.mixin.client.screen;

import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.event.Event;
import me.allinkdev.deviousmod.event.screen.impl.SetScreenEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class ScreenOverrider {
    @Shadow
    @Nullable
    public Screen currentScreen;

    @Shadow
    @Final
    public Mouse mouse;
    @Shadow
    public boolean skipGameRender;
    @Shadow
    @Nullable
    public ClientWorld world;
    @Shadow
    @Nullable
    public ClientPlayerEntity player;
    @Shadow
    @Final
    private Window window;
    @Shadow
    @Final
    private SoundManager soundManager;

    @Shadow
    public abstract void updateWindowTitle();

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void onSetScreen(final Screen providedScreen, final CallbackInfo ci) {
        final SetScreenEvent event = new SetScreenEvent(providedScreen);
        final EventBus eventBus = Event.getEventBus();
        eventBus.post(event);

        Screen overriddenScreen = event.getTarget();

        if (overriddenScreen == null) {
            overriddenScreen = onNullScreen();
        }

        final Class<? extends Screen> clazz = providedScreen == null ? Screen.class : providedScreen.getClass();

        customSetScreen(overriddenScreen);
        ci.cancel();
    }

    private @Nullable Screen onNullScreen() {
        if (this.world == null || this.player == null) {
            return new TitleScreen();
        }

        if (!this.player.isDead()) {
            return null;
        }

        if (!this.player.showsDeathScreen()) {
            this.player.requestRespawn();
            return null;
        }

        final ClientWorld.Properties levelProperties = this.world.getLevelProperties();
        final boolean hardCore = levelProperties.isHardcore();
        return new DeathScreen(null, hardCore);
    }

    private void customSetScreen(@Nullable final Screen screen) {
        if (currentScreen != null) {
            currentScreen.removed();
        }

        currentScreen = screen;
        BufferRenderer.reset();
        if (screen != null) {
            this.mouse.unlockCursor();
            KeyBinding.unpressAll();
            screen.init((MinecraftClient) (Object) this, window.getScaledWidth(), window.getScaledHeight());
            this.skipGameRender = false;
        } else {
            this.soundManager.resumeAll();
            this.mouse.lockCursor();
        }

        this.updateWindowTitle();
    }
}
