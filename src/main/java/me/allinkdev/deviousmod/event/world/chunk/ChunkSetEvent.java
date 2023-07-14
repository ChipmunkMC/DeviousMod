package me.allinkdev.deviousmod.event.world.chunk;

import com.github.allinkdev.deviousmod.api.event.Event;
import me.allinkdev.deviousmod.event.Async;
import net.minecraft.world.chunk.WorldChunk;

@Async
public final class ChunkSetEvent implements Event {
    private final int index;
    private final WorldChunk chunk;

    public ChunkSetEvent(final int index, final WorldChunk chunk) {
        this.index = index;
        this.chunk = chunk;
    }

    public int getIndex() {
        return this.index;
    }

    public WorldChunk getChunk() {
        return this.chunk;
    }
}
