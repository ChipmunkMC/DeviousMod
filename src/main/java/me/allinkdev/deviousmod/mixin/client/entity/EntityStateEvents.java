package me.allinkdev.deviousmod.mixin.client.entity;

import me.allinkdev.deviousmod.event.entity.impl.EntityAddEvent;
import me.allinkdev.deviousmod.event.entity.impl.EntityRemoveEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.client.world.ClientEntityManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.entity.EntityLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientEntityManager.class)
public final class EntityStateEvents {
    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    private void onAddEntity(final EntityLike entityLike, final CallbackInfo ci) {
        if (!(entityLike instanceof final Entity entity)) return;
        if (EventUtil.postCancellable(new EntityAddEvent(entity))) ci.cancel();
    }

    @Mixin(ClientWorld.class)
    private static final class EntityRemoval {
        @Redirect(method = "removeEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityLookup;get(I)Lnet/minecraft/world/entity/EntityLike;"))
        private EntityLike onRemoveEntity(final EntityLookup instance, final int id) {
            final EntityLike entityLike = instance.get(id);

            if (!(entityLike instanceof final Entity entity)) return entityLike;
            if (!EventUtil.postCancellable(new EntityRemoveEvent(entity))) return entity;
            return null;
        }
    }
}
