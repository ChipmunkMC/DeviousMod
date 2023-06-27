package me.allinkdev.deviousmod.mixin.client.world;

import me.allinkdev.deviousmod.event.world.chunk.BlockStateUpdateEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public abstract class BlockUpdateListener extends World {

    protected BlockUpdateListener(final MutableWorldProperties properties, final RegistryKey<World> registryRef, final DynamicRegistryManager registryManager,
                                  final RegistryEntry<DimensionType> dimensionEntry, final Supplier<Profiler> profiler, final boolean isClient,
                                  final boolean debugWorld, final long biomeAccess, final int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Override
    public void onBlockChanged(final BlockPos pos, final BlockState oldBlock, final BlockState newBlock) {
        EventUtil.postEvent(new BlockStateUpdateEvent(pos, oldBlock, newBlock));
    }
}
