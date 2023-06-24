package me.allinkdev.deviousmod.account;

import me.allinkdev.deviousmod.DeviousMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.util.Uuids;

import java.util.Optional;

public class AccountManager {
    private final MinecraftClient client;
    private final Session originalSession;

    public AccountManager(final MinecraftClient client, final DeviousMod deviousMod) {
        this.client = client;
        this.originalSession = cloneSession(client.session);
        deviousMod.getEventManager().registerListener(this);
    }

    private static Session cloneSession(final Session original) {
        return new Session(
                original.getUsername(),
                original.getUuid(),
                original.getAccessToken(),
                original.getXuid(),
                original.getClientId(),
                original.getAccountType()
        );
    }

    public void setSession(final Session session) {
        this.client.session = session;
    }

    public String getOriginalUsername() {
        return this.originalSession.getUsername();
    }

    public void setUsername(final String username) {
        this.client.session = new Session(username, Uuids.getOfflinePlayerUuid(username).toString(), "", Optional.empty(), Optional.empty(), Session.AccountType.LEGACY);
    }

    public void restoreSession() {
        this.client.session = this.originalSession;
    }
}
