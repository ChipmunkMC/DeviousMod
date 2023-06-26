package me.allinkdev.deviousmod.event.render.block;

import com.github.allinkdev.deviousmod.api.event.Cancellable;
import net.minecraft.block.entity.BlockEntity;

import java.util.Collections;
import java.util.Set;

public class PreUncullableBlockEntityRenderEvent extends Cancellable {
    private final Set<BlockEntity> blockEntities;

    public PreUncullableBlockEntityRenderEvent(final Set<BlockEntity> blockEntities) {
        this.blockEntities = blockEntities;
    }

    public Set<BlockEntity> getBlockEntities() {
        return Collections.unmodifiableSet(this.blockEntities);
    }
}
