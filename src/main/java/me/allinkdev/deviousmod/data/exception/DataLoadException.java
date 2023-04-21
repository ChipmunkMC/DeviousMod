package me.allinkdev.deviousmod.data.exception;

import java.io.File;

public final class DataLoadException extends RuntimeException {
    public DataLoadException(final File file, final Throwable cause) {
        super(String.format("Failed to load %s!", file), cause);
    }
}
