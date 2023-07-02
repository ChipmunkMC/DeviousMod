package me.allinkdev.deviousmod.mixin.client.screen;

import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.title.panorama.PanoramaDirectorySelectEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.util.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.concurrent.Executors;

@Mixin(TitleScreen.class)
public final class PanoramaListener {
    private static final Identifier PANORAMA_IDENTIFIER = new Identifier("textures/gui/title/background/panorama");
    @Shadow
    @Final
    public static CubeMapRenderer PANORAMA_CUBE_MAP;

    @Redirect(method = "<init>(ZLnet/minecraft/client/gui/LogoDrawer;)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/TitleScreen;PANORAMA_CUBE_MAP:Lnet/minecraft/client/gui/CubeMapRenderer;", opcode = Opcodes.H_GETFIELD))
    private CubeMapRenderer onGetCubeMap() {
        final Identifier directory = EventUtil.postEvent(new PanoramaDirectorySelectEvent(PANORAMA_IDENTIFIER)).getTextureDirectory();

        if (directory.equals(PANORAMA_IDENTIFIER)) {
            return PANORAMA_CUBE_MAP;
        }

        final CubeMapRenderer cubeMapRenderer = new CubeMapRenderer(directory);
        cubeMapRenderer.loadTexturesAsync(DeviousMod.CLIENT.getTextureManager(), Executors.newSingleThreadExecutor());
        return cubeMapRenderer;
    }
}
