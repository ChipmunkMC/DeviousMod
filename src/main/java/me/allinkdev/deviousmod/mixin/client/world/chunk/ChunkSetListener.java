package me.allinkdev.deviousmod.mixin.client.world.chunk;

import me.allinkdev.deviousmod.event.world.chunk.ChunkSetEvent;
import me.allinkdev.deviousmod.util.EventUtil;
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
        EventUtil.postEvent(new ChunkSetEvent(index, chunk));
    }
}
