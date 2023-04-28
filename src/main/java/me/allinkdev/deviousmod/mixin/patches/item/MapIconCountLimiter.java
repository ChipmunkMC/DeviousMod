package me.allinkdev.deviousmod.mixin.patches.item;

import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.text.Text;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(MapState.class)
public final class MapIconCountLimiter {
    @Shadow
    @Final
    Map<String, MapIcon> icons;

    @Inject(method = "addIcon", at = @At("HEAD"), cancellable = true)
    private void onAddIcon(final MapIcon.Type type, final WorldAccess world, final String key,
                           final double x, final double z,
                           final double rotation, final Text text, final CallbackInfo ci) {
        final int iconCount = this.icons.size();

        if (iconCount < 15) {
            return;
        }

        ci.cancel();
    }
}
