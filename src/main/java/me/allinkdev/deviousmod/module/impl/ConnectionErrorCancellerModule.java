package me.allinkdev.deviousmod.module.impl;

import com.google.common.eventbus.Subscribe;
import com.github.allinkdev.deviousmod.api.experiments.Experimental;
import me.allinkdev.deviousmod.event.network.connection.ConnectionErrorEvent;
import me.allinkdev.deviousmod.module.DModule;
import me.allinkdev.deviousmod.module.DModuleManager;

@Experimental(value = "Just downright unsafe.", hide = false)
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
        return "Cancels all connection errors.";
    }

    @Subscribe
    public void onConnectionError(final ConnectionErrorEvent event) {
        event.cancel();

        /*final Throwable throwable = event.getThrowable();
        deviousMod.sendMessage(Component.text("Cancelled connection error: ", NamedTextColor.RED)
                .append(Component.text(ThrowableUtil.getStackTrace(throwable, 0), NamedTextColor.RED)));*/
    }
}
