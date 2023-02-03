package me.allinkdev.deviousmod.mixin.client;

import me.allinkdev.deviousmod.event.entity.impl.EntityAddEvent;
import me.allinkdev.deviousmod.event.entity.impl.EntityRemoveEvent;
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
public class EntityEvents {
    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    public void onAddEntity(final EntityLike entityLike, final CallbackInfo ci) {
        if (!(entityLike instanceof final Entity entity)) {
            return;
        }

        final boolean cancelled = EntityAddEvent.addEntity(entity);

        if (!cancelled) {
            return;
        }

        ci.cancel();
    }

    @Mixin(ClientWorld.class)
    private static class EntityRemoval {
        @Redirect(method = "removeEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityLookup;get(I)Lnet/minecraft/world/entity/EntityLike;"))
        public EntityLike onRemoveEntity(final EntityLookup instance, final int id) {
            final EntityLike entityLike = instance.get(id);

            if (!(entityLike instanceof final Entity entity)) {
                return entityLike;
            }

            final boolean cancelled = EntityRemoveEvent.removeEntity(entity);

            if (!cancelled) {
                return entity;
            }

            return null;
        }
    }
}
