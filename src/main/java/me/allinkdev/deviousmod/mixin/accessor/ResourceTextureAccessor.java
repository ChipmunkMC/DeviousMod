package me.allinkdev.deviousmod.mixin.accessor;

import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ResourceTexture.class)
public interface ResourceTextureAccessor {
    @Accessor
    Identifier getLocation();
}
