package me.allinkdev.deviousmod.mixin.accessor;

import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(MapState.class)
public interface MapStateAccessor {
    @Accessor
    Map<String, MapIcon> getIcons();
}
