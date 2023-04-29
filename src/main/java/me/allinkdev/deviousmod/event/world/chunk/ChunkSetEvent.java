package me.allinkdev.deviousmod.event.world.chunk;

import me.allinkdev.deviousmod.event.Event;
import net.minecraft.world.chunk.WorldChunk;

public final class ChunkSetEvent extends Event {
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
