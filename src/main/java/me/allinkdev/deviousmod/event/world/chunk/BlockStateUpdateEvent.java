package me.allinkdev.deviousmod.event.world.chunk;

import com.github.allinkdev.deviousmod.api.event.Event;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public final class BlockStateUpdateEvent implements Event {
    private final BlockPos pos;
    private final BlockState oldState;
    private final BlockState newState;

    public BlockStateUpdateEvent(final BlockPos pos, final BlockState oldState, final BlockState newState) {
        this.pos = pos;
        this.oldState = oldState;
        this.newState = newState;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockState getOldState() {
        return this.oldState;
    }

    public BlockState getNewState() {
        return this.newState;
    }
}
