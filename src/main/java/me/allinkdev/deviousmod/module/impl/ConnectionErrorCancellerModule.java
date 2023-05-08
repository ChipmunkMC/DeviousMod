package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import me.allinkdev.deviousmod.event.network.connection.ConnectionErrorEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.DModuleManager;

public final class ConnectionErrorCancellerModule extends DModule {
    public ConnectionErrorCancellerModule(final DModuleManager moduleManager) {
        super(moduleManager);
    }

    @Override
    public String getCategory() {
        return "Network";
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

        /*final Throwable throwable = event.getThrowable();
        deviousMod.sendMessage(Component.text("Cancelled connection error: ", NamedTextColor.RED)
                .append(Component.text(ThrowableUtil.getStackTrace(throwable, 0), NamedTextColor.RED)));*/
    }
}
