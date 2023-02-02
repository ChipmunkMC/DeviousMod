package me.allinkdev.deviousmod.mixin.client;

import me.allinkdev.deviousmod.event.screen.impl.ScreenOpenEvent;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Optional;

@Mixin(MinecraftClient.class)
public abstract class ScreenOverrider {
    private final Logger logger = LoggerFactory.getLogger("DeviousMod/ScreenOverrider");
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

    @Inject(method = "setScreen", at = @At(value = "HEAD"), cancellable = true)
    public void onSetScreen(final Screen providedScreen, final CallbackInfo ci) {
        final Optional<Screen> newScreenOptional = ScreenOpenEvent.getOverriddenScreen(providedScreen);
        final boolean empty = newScreenOptional.isEmpty();

        if (empty && providedScreen == null) {
            // Let vanilla code handle this
            return;
        }

        final Class<? extends Screen> clazz = providedScreen == null ? Screen.class : providedScreen.getClass();
        final String name = clazz.getTypeName();
        logger.info("Processing setScreen {}...", name);

        final Screen screen = newScreenOptional.orElse(onNullScreen());

        if (Objects.equals(screen, providedScreen)) {
            logger.info("Not overriding!");
            return;
        }

        customSetScreen(screen);
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
            final Class<? extends Screen> clazz = screen.getClass();
            final String name = clazz.getTypeName();
            logger.info("Overriding with {}!", name);
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
