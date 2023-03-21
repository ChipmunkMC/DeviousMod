package me.allinkdev.deviousmod.command;

import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public final class CommandCompletionManager {
    private static final AtomicInteger completionId = new AtomicInteger();
    private static final Map<Integer, Consumer<Suggestions>> futureMap = new HashMap<>();

    public static boolean handleCompletion(final int id, final Suggestions suggestions) {
        if (!futureMap.containsKey(id)) {
            return false;
        }

        final Consumer<Suggestions> consumer = futureMap.get(id);
        consumer.accept(suggestions);
        futureMap.remove(id);
        return true;
    }

    public static void getCompletion(final String command, final Consumer<Suggestions> consumer) {
        final int id = completionId.decrementAndGet();
        futureMap.put(id, consumer);

        final MinecraftClient client = MinecraftClient.getInstance();
        final ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();

        if (networkHandler == null) {
            throw new IllegalStateException("Completion requested in invalid state!");
        }

        final RequestCommandCompletionsC2SPacket packet = new RequestCommandCompletionsC2SPacket(id, command);
        networkHandler.sendPacket(packet);
    }
}
