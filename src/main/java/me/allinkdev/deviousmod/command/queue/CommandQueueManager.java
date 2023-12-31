package me.allinkdev.deviousmod.command.queue;

import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.network.connection.ConnectionEndEvent;
import me.allinkdev.deviousmod.event.network.connection.ConnectionStartEvent;
import me.allinkdev.deviousmod.event.self.chat.impl.SelfSendCommandEvent;
import me.allinkdev.deviousmod.event.time.tick.impl.ClientTickEndEvent;
import me.allinkdev.deviousmod.util.EventUtil;
import net.lenni0451.lambdaevents.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Deque;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Predicate;

public final class CommandQueueManager {
    private final Deque<String> commandDeque = new ConcurrentLinkedDeque<>();
    private final Set<String> sending = new ConcurrentSkipListSet<>();
    private long lastExecution = 0;

    public CommandQueueManager() {
        EventUtil.registerListener(this);
    }

    public void reset() {
        this.commandDeque.clear();
        this.lastExecution = 0;
    }

    @EventHandler
    public void onConnectionStart(final ConnectionStartEvent event) {
        this.reset();
    }

    @EventHandler
    public void onConnectionEnd(final ConnectionEndEvent event) {
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

    public void purgeIf(final Predicate<? super String> predicate) {
        this.commandDeque.removeIf(predicate);
    }

    public void purgeInstancesOf(final String command) {
        this.purgeIf(c -> c.equals(command));
    }

    @EventHandler
    public void onCommandSend(final SelfSendCommandEvent event) {
        if (event.wasQueued()) {
            return;
        }

        this.lastExecution = System.currentTimeMillis();
    }

    @EventHandler
    public void onTickEnd(final ClientTickEndEvent event) {
        final long now = System.currentTimeMillis();
        final long diff = now - this.lastExecution;

        if (diff < 200) {
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
        this.lastExecution = now;
    }
}
