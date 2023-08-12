package me.allinkdev.deviousmod.mixin.patches.auth;

import com.mojang.authlib.yggdrasil.TextureUrlChecker;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = TextureUrlChecker.class, remap = false)
public abstract class PromiscuousTextureUrlBlocker {
    @Shadow
    @Final
    private static List<String> ALLOWED_DOMAINS;

    @Redirect(method = "isAllowedTextureDomain", at = @At(value = "INVOKE", target = "Lcom/mojang/authlib/yggdrasil/TextureUrlChecker;isDomainOnList(Ljava/lang/String;Ljava/util/List;)Z"))
    private static boolean deviousmod$isAllowedTextureDomain$isDomainOnList(final String entry, final List<String> domain) {
        return domain.equals(ALLOWED_DOMAINS) ? entry.startsWith("textures.minecraft.net") : isDomainOnList(entry, domain);
    }

    @Shadow
    private static boolean isDomainOnList(final String domain, final List<String> list) {
        return false;
    }
}
