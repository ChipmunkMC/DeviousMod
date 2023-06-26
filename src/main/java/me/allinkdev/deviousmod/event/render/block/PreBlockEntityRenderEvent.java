package me.allinkdev.deviousmod.event.render.block;

import com.github.allinkdev.deviousmod.api.event.Cancellable;
import net.minecraft.block.entity.BlockEntity;

public class PreBlockEntityRenderEvent extends Cancellable {
    private final BlockEntity blockEntity;

    public PreBlockEntityRenderEvent(final BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    public BlockEntity getBlockEntity() {
        return this.blockEntity;
    }
}
