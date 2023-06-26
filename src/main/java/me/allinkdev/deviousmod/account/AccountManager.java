package me.allinkdev.deviousmod.account;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.DeviousMod;
import me.allinkdev.deviousmod.event.screen.impl.InitScreenEvent;
import me.allinkdev.deviousmod.mixin.accessor.ScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.Session;
import net.minecraft.text.Text;
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

    private void operateOnScreen(final Screen screen, final ScreenAccessor accessor) {
        final int width = 200;
        final int height = 10;
        final TextFieldWidget textFieldWidget = new TextFieldWidget(this.client.textRenderer, screen.width - (width + 4), screen.height - (height + 4), width, height, Text.empty());
        textFieldWidget.setMaxLength(16);
        textFieldWidget.setText(this.getCurrentUsername());
        textFieldWidget.setChangedListener(this::setUsername);
        accessor.invokeAddDrawableChild(textFieldWidget);
    }

    @Subscribe
    public void onScreenInit(final InitScreenEvent event) {
        if (!(event.getScreen() instanceof MultiplayerScreen)) {
            return;
        }

        event.addConsumer(this::operateOnScreen);
    }

    public void setSession(final Session session) {
        this.client.session = session;
    }

    public String getOriginalUsername() {
        return this.originalSession.getUsername();
    }

    public void setUsername(final String username) {
        if (username.equals(this.getOriginalUsername()) || username.isEmpty() || username.length() > 16) {
            this.restoreSession();
            return;
        }

        this.client.session = new Session(username, Uuids.getOfflinePlayerUuid(username).toString(), "", Optional.empty(), Optional.empty(), Session.AccountType.LEGACY);
    }

    public String getCurrentUsername() {
        return this.client.session.getUsername();
    }

    public void restoreSession() {
        this.client.session = this.originalSession;
    }
}
