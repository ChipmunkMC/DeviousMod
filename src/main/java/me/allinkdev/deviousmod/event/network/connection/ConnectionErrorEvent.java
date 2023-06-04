package me.allinkdev.deviousmod.event.network.connection;

import com.github.allinkdev.deviousmod.api.event.Cancellable;

public final class ConnectionErrorEvent extends Cancellable {
    private final Throwable throwable;

    public ConnectionErrorEvent(final Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
