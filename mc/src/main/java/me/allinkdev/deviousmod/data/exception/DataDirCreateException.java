package me.allinkdev.deviousmod.data.exception;

import java.io.File;

public class DataDirCreateException extends RuntimeException {
    public DataDirCreateException(final File file) {
        super(String.format("Failed to create directory %s!", file));
    }
}
