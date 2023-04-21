package me.allinkdev.deviousmod.util;

import me.allinkdev.deviousmod.DeviousMod;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.util.Session;
import net.minecraft.network.ClientConnection;
import net.minecraft.util.Util;
import net.minecraft.util.Uuids;

import java.util.UUID;

public final class UUIDUtil extends NoConstructor {
    public static UUID getSelfUUID() {
        final ClientPlayNetworkHandler clientPlayNetworkHandler = DeviousMod.CLIENT.getNetworkHandler();

        if (clientPlayNetworkHandler == null) {
            return Util.NIL_UUID;
        }

        final ClientConnection connection = clientPlayNetworkHandler.getConnection();
        final boolean encrypted = connection.isEncrypted();
        final Session session = DeviousMod.CLIENT.getSession();
        final String nickname = session.getUsername();
        final UUID onlineUUID = session.getUuidOrNull();

        return encrypted ? onlineUUID : Uuids.getOfflinePlayerUuid(nickname);
    }
}
