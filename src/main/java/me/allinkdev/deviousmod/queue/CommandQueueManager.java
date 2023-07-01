package me.allinkdev.deviousmod.queue;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.network.connection.ConnectionEndEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionStartEvent;
import me.allinkdev.deviousmod.event.tick.impl.ClientTickEndEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Deque;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListSet;

public final class CommandQueueManager {
    private final Deque<String> commandDeque = new ConcurrentLinkedDeque<>();
    private final Set<String> sending = new ConcurrentSkipListSet<>();
    private long currentTick = 0;

    public CommandQueueManager() {
        EventUtil.registerListener(this);
    }

    public void reset() {
        this.commandDeque.clear();
        this.currentTick = 0;
    }

    @Subscribe
    private void onConnectionStart(final ConnectionStartEvent event) {
        this.reset();
    }

    @Subscribe
    private void onConnectionEnd(final ConnectionEndEvent event) {
        this.reset();
    }

    public void addCommandToFront(final String command) {
        this.commandDeque.offerFirst(command);
    }

    public void addCommandToBack(final String command) {
        this.commandDeque.offerLast(command);
    }

    public void addCommandsToBack(final Collection<String> commands) {
        commands.forEach(this::addCommandToBack);
    }

    public void addCommandsToFront(final Collection<String> commands) {
        commands.forEach(this::addCommandToFront);
    }

    public boolean isQueuedAndIfSoRemove(final String command) {
        return this.sending.remove(command);
    }

    @Subscribe
    private void onTickEnd(final ClientTickEndEvent event) {
        this.currentTick++;

        if (this.currentTick % 4 != 0) {
            return;
        }

        final String command = commandDeque.poll();

        if (command == null) {
            return;
        }

        final MinecraftClient client = DeviousMod.CLIENT;
        final ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();

        if (networkHandler == null) {
            return;
        }

        final String normalizedCommand = StringUtils.normalizeSpace(command);
        this.sending.add(normalizedCommand);
        networkHandler.sendChatCommand(normalizedCommand);
    }
}
