package me.allinkdev.deviousmod.mixin.client.entity;

import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;
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
public final class EntityStateEvents {
    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    private void onAddEntity(final EntityLike entityLike, final CallbackInfo ci) {
        if (!(entityLike instanceof final Entity entity)) {
            return;
        }

        final EntityAddEvent event = new EntityAddEvent(entity);
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventBus eventBus = deviousMod.getEventBus();
        eventBus.post(event);

        if (!event.isCancelled()) {
            return;
        }

        ci.cancel();
    }

    @Mixin(ClientWorld.class)
    private static final class EntityRemoval {
        @Redirect(method = "removeEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityLookup;get(I)Lnet/minecraft/world/entity/EntityLike;"))
        private EntityLike onRemoveEntity(final EntityLookup instance, final int id) {
            final EntityLike entityLike = instance.get(id);

            if (!(entityLike instanceof final Entity entity)) {
                return entityLike;
            }

            final EntityRemoveEvent event = new EntityRemoveEvent(entity);
            final DeviousMod deviousMod = DeviousMod.getInstance();
            final EventBus eventBus = deviousMod.getEventBus();
            eventBus.post(event);

            if (!event.isCancelled()) {
                return entity;
            }

            return null;
        }
    }
}
