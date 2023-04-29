package me.allinkdev.deviousmod.mixin.client.world.chunk;

import com.google.common.eventbus.EventBus;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.world.chunk.ChunkSetEvent;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientChunkManager.ClientChunkMap.class)
public final class ChunkSetListener {
    @Inject(method = "set", at = @At("TAIL"))
    private void onSet(final int index, final WorldChunk chunk, final CallbackInfo ci) {
        final DeviousMod deviousMod = DeviousMod.getInstance();
        final EventBus eventBus = deviousMod.getEventBus();
        final ChunkSetEvent chunkSetEvent = new ChunkSetEvent(index, chunk);

        eventBus.post(chunkSetEvent);
    }
}
