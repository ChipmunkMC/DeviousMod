package me.allinkdev.deviousmod.event.world.chunk;

import me.allinkdev.deviousmod.event.Event;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public final class BlockStateUpdateEvent extends Event {
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
