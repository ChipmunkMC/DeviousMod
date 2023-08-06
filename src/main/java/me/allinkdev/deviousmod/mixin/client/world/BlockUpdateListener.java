package me.allinkdev.deviousmod.mixin.client.world;

import me.allinkdev.deviousmod.event.world.chunk.BlockStateUpdateEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public final class BlockUpdateListener {
    @Shadow @Final public boolean isClient;

    @Inject(method = "onBlockChanged", at = @At("HEAD"))
    public void onBlockChanged(final BlockPos pos, final BlockState oldBlock, final BlockState newBlock, final CallbackInfo ci) {
        if (!this.isClient) return;
        EventUtil.postEvent(new BlockStateUpdateEvent(pos, oldBlock, newBlock));
    }
}
