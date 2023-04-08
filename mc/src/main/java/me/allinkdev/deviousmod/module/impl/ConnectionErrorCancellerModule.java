package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.event.network.connection.ConnectionErrorEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.ModuleManager;
import me.allinkdev.deviousmod.util.ThrowableUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class ConnectionErrorCancellerModule extends DModule {
    public ConnectionErrorCancellerModule(final ModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    public String getModuleName() {
        return "ConnectionErrorCanceller";
    }

    @Override
    public String getDescription() {
        return "Cancels all connection errors. WARNING: Really, really unsafe.";
    }

    @Subscribe
    public void onConnectionError(final ConnectionErrorEvent event) {
        event.setCancelled(true);

        final Throwable throwable = event.getThrowable();
        deviousMod.sendMessage(Component.text("Cancelled connection error: ", NamedTextColor.RED)
                .append(Component.text(ThrowableUtil.getStackTrace(throwable, 0), NamedTextColor.RED)));
    }
}
