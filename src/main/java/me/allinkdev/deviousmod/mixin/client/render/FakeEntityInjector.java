package me.allinkdev.deviousmod.mixin.client.render;

import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.entity.impl.PreEntitiesRenderEvent;
import me.allinkdev.deviousmod.util.IterUtil;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(WorldRenderer.class)
public final class FakeEntityInjector {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getEntities()Ljava/lang/Iterable;"))
    private Iterable<Entity> onRender(final ClientWorld instance) {
        final Iterable<Entity> entityIterable = instance.getEntities();
        final List<Entity> entities = IterUtil.toList(entityIterable);
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventBus eventBus = deviousMod.getEventBus();
        final PreEntitiesRenderEvent event = new PreEntitiesRenderEvent(entities);
        eventBus.post(event);

        return entities;
    }
}