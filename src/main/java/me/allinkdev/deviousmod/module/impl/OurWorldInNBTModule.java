package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.entity.impl.PreEntitiesRenderEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionEndEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionStartEvent;
import me.allinkdev.deviousmod.event.network.packet.impl.PacketS2CEvent;
import me.allinkdev.deviousmod.event.world.chunk.BlockStateUpdateEvent;
import me.allinkdev.deviousmod.event.world.chunk.ChunkSetEvent;
import me.allinkdev.deviousmod.mixin.accessor.ClientChunkManagerAccessor;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import me.allinkdev.deviousmod.query.QueryManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import java.util.*;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class OurWorldInNBTModule extends DModule {
    private static final int MAX_LENGTH = 2_000;
    private final Map<BlockPos, DisplayEntity.TextDisplayEntity> blockPosToTextDisplay = new HashMap<>();

    public OurWorldInNBTModule(final DModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    public String getCategory() {
        return "Render";
    }

    @Override
    public String getModuleName() {
        return "OurWorldInNBT";
    }

    @Override
    public String getDescription() {
        return "Displays the NBT of every block entity in render distance.";
    }

    @Subscribe
    private void onConnectionEnd(final ConnectionEndEvent event) {
        this.blockPosToTextDisplay.clear();
    }

    @Subscribe
    private void onConnectionStart(final ConnectionStartEvent event) {
        this.blockPosToTextDisplay.clear();
    }

    private void putDisplayEntity(final BlockPos blockPos, final BlockEntity blockEntity, final NbtCompound compound) {
        synchronized (this.blockPosToTextDisplay) {
            final World world = blockEntity.getWorld();
            final DisplayEntity.TextDisplayEntity textDisplayEntity = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, world);
            final Vec3d entityPos = blockPos.up().toCenterPos();
            final double entityX = entityPos.getX();
            final double entityY = entityPos.getY();
            final double entityZ = entityPos.getZ();

            textDisplayEntity.setPos(entityX, entityY, entityZ);

            final String stringifiedNbt = compound.asString();
            final int nbtLength = stringifiedNbt.length();
            final Text nbtComponent;

            if (nbtLength >= MAX_LENGTH) {
                nbtComponent = Text.of(stringifiedNbt.substring(0, MAX_LENGTH) + "...");
            } else {
                nbtComponent = NbtHelper.toPrettyPrintedText(compound);
            }

            final DataTracker dataTracker = textDisplayEntity.getDataTracker();
            dataTracker.set(DisplayEntity.TextDisplayEntity.TEXT, nbtComponent);
            dataTracker.set(DisplayEntity.BILLBOARD, DisplayEntity.BillboardMode.VERTICAL.getIndex());
            dataTracker.set(DisplayEntity.TextDisplayEntity.LINE_WIDTH, 350);
            this.blockPosToTextDisplay.put(blockPos, textDisplayEntity);
        }
    }

    private void updateMeta(final BlockPos blockPos, final BlockEntity blockEntity) {
        QueryManager.scheduleBlockQuery(blockPos)
                .thenAccept(n -> this.putDisplayEntity(blockPos, blockEntity, n));
    }

    @Subscribe
    public void onChunkSet(final ChunkSetEvent event) {
        final WorldChunk worldChunk = event.getChunk();

        this.scanChunk(worldChunk);
    }

    private void genericBlockUpdate(final BlockPos blockPos) {
        final ClientWorld clientWorld = DeviousMod.CLIENT.world;

        if (clientWorld == null) {
            return;
        }

        final BlockEntity blockEntity = clientWorld.getBlockEntity(blockPos);

        if (blockEntity == null) {
            blockPosToTextDisplay.remove(blockPos);
            return;
        }

        this.updateMeta(blockPos, blockEntity);
    }

    @Subscribe
    public void onBlockStateUpdate(final BlockStateUpdateEvent event) {
        final BlockPos blockPos = event.getPos();

        this.genericBlockUpdate(blockPos);
    }

    @Override
    public void onEnable() {
        final ClientWorld world = client.world;

        if (world == null) {
            return;
        }

        final ClientChunkManager clientChunkManager = world.getChunkManager();
        final ClientChunkManagerAccessor chunkManagerAccessor = (ClientChunkManagerAccessor) clientChunkManager;
        final ClientChunkManager.ClientChunkMap clientChunkMap = chunkManagerAccessor.getChunks();

        final AtomicReferenceArray<WorldChunk> chunks = clientChunkMap.chunks;
        final int chunkLength = chunks.length();
        final List<WorldChunk> chunkList = new ArrayList<>();

        for (int i = 0; i < chunkLength; i++) {
            final WorldChunk chunk = chunks.get(i);

            if (chunk == null) {
                continue;
            }

            chunkList.add(chunk);
        }

        chunkList.forEach(this::scanChunk);
    }

    @Override
    public void onDisable() {
        this.blockPosToTextDisplay.clear();
    }

    private void scanChunk(final WorldChunk worldChunk) {
        worldChunk.getBlockEntities()
                .forEach(this::updateMeta);
    }

    @Subscribe
    public void onPacketReceive(final PacketS2CEvent event) {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof final BlockEventS2CPacket blockEventPacket) {
            final BlockPos pos = blockEventPacket.getPos();

            this.genericBlockUpdate(pos);
        } else if (packet instanceof final BlockEntityUpdateS2CPacket updatePacket) {
            final BlockPos blockPos = updatePacket.getPos();

            this.genericBlockUpdate(blockPos);
        }
    }

    @Subscribe
    public void onPreEntitiesRender(final PreEntitiesRenderEvent event) {
        final ClientPlayerEntity player = client.player;

        if (player == null) {
            return;
        }

        synchronized (this.blockPosToTextDisplay) {
            final Vec3d position = player.getPos();
            final Collection<DisplayEntity.TextDisplayEntity> textDisplayEntities = this.blockPosToTextDisplay.values()
                    .stream()
                    .filter(e -> e.getPos().squaredDistanceTo(position) <= 45)
                    .toList();

            event.addEntities(textDisplayEntities);
        }
    }
}
