package me.allinkdev.deviousmod.module.impl;

import com.github.allinkdev.deviousmod.api.experiments.Experimental;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.network.connection.ConnectionEndEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionStartEvent;
import me.allinkdev.deviousmod.event.network.packet.impl.PacketS2CEvent;
import me.allinkdev.deviousmod.event.time.second.ClientSecondEvent;
import me.allinkdev.deviousmod.event.world.chunk.BlockStateUpdateEvent;
import me.allinkdev.deviousmod.event.world.chunk.ChunkSetEvent;
import me.allinkdev.deviousmod.module.CommandDependentModule;
import me.allinkdev.deviousmod.module.DModuleManager;
import net.lenni0451.lambdaevents.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.BlockPos;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Experimental(value = "Only works if you join the game with it enabled. Otherwise, it requires you to reload the chunks.", hide = false)
public final class AntiIcuModule extends CommandDependentModule {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final List<BlockPos> commandBlocks = Collections.synchronizedList(new ArrayList<>());
    private boolean icuControlled;
    private long teleportsThisSecond;

    public AntiIcuModule(final DModuleManager moduleManager) {
        super(moduleManager, "icontrolu:icu");
    }

    @Override
    public String getModuleName() {
        return "AntiIcu";
    }

    @Override
    public String getDescription() {
        return "Prevents you from being icu controlled.";
    }

    @Override
    public String getCategory() {
        return "Kaboom";
    }

    private void reset() {
        this.teleportsThisSecond = 0;
        this.icuControlled = false;
        this.commandBlocks.clear();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.reset();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.reset();
    }

    @EventHandler
    public void onConnectionEnd(final ConnectionEndEvent event) {
        this.reset();
    }

    @EventHandler
    public void onConnectionStart(final ConnectionStartEvent event) {
        this.reset();
    }

    @EventHandler
    public void onClientSecond(final ClientSecondEvent event) {
        this.icuControlled = this.teleportsThisSecond >= 15;

        this.teleportsThisSecond = 0;

        if (!this.icuControlled || !this.commandPresent) {
            return;
        }

        if (this.commandBlocks.isEmpty() || (this.client.player != null && !this.client.player.hasPermissionLevel(4))) {
            DeviousMod.LOGGER.info("No command blocks, or not op, disconnecting!");
            this.client.disconnect();
            return;
        }

        final ClientPlayNetworkHandler networkHandler = this.client.getNetworkHandler();
        if (networkHandler == null) {
            return;
        }

        final int size = this.commandBlocks.size();
        final int index = SECURE_RANDOM.nextInt(size);
        final BlockPos element = this.commandBlocks.get(index);
        networkHandler.sendPacket(new UpdateCommandBlockC2SPacket(element, "execute run op @a", CommandBlockBlockEntity.Type.AUTO, false, false, true));
        networkHandler.sendPacket(new UpdateCommandBlockC2SPacket(element, "sudo * icu stop", CommandBlockBlockEntity.Type.AUTO, false, false, true));
    }

    @EventHandler
    public void onChunkSet(final ChunkSetEvent event) {
        event.getChunk().getBlockEntities().entrySet().stream()
                .peek(e -> this.commandBlocks.remove(e.getKey()))
                .filter(e -> e.getValue().getType().equals(BlockEntityType.COMMAND_BLOCK))
                .forEach(e -> this.commandBlocks.add(e.getKey()));
    }

    @EventHandler
    public void onBlockUpdate(final BlockStateUpdateEvent event) {
        final BlockPos blockPos = event.getPos();

        if (!event.getNewState().getBlock().equals(Blocks.COMMAND_BLOCK) && !event.getNewState().getBlock().equals(Blocks.CHAIN_COMMAND_BLOCK) && !event.getNewState().getBlock().equals(Blocks.REPEATING_COMMAND_BLOCK)) {
            this.commandBlocks.remove(blockPos);
        } else {
            this.commandBlocks.add(blockPos);
        }
    }

    @EventHandler
    public void onPacketReceive(final PacketS2CEvent event) {
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket) {
            this.teleportsThisSecond++;
        }
    }
}
