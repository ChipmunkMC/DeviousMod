package me.allinkdev.deviousmod.data.exception;

import java.io.File;

public final class DataDirCreateException extends RuntimeException {
    public DataDirCreateException(final File file) {
        super(String.format("Failed to create directory %s!", file));
    }
}
