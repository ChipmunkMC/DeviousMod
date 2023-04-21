package me.allinkdev.deviousmod.command;

import com.mojang.brigadier.suggestion.Suggestions;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.util.NoConstructor;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public final class CommandCompletionManager extends NoConstructor {
    private static final AtomicInteger completionId = new AtomicInteger();
    private static final Map<Integer, CompletableFuture<Suggestions>> futureMap = new HashMap<>();

    public static boolean handleCompletion(final int id, final Suggestions suggestions) {
        if (!futureMap.containsKey(id)) {
            return false;
        }

        final CompletableFuture<Suggestions> consumer = futureMap.get(id);
        consumer.complete(suggestions);
        futureMap.remove(id);
        return true;
    }

    public static CompletableFuture<Suggestions> getCompletion(final String command) {
        final CompletableFuture<Suggestions> future = new CompletableFuture<>();

        final int id = completionId.decrementAndGet();
        futureMap.put(id, future);

        final ClientPlayNetworkHandler networkHandler = DeviousMod.CLIENT.getNetworkHandler();

        if (networkHandler == null) {
            throw new IllegalStateException("Completion requested in invalid state!");
        }

        final RequestCommandCompletionsC2SPacket packet = new RequestCommandCompletionsC2SPacket(id, command);
        networkHandler.sendPacket(packet);

        return future;
    }
}
