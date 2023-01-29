package me.allinkdev.deviousmod.data.exception;

import java.io.File;

public class DataSaveException extends RuntimeException {
    public DataSaveException(final File file, final Throwable cause) {
        super(String.format("Failed to save %s!", file), cause);
    }
}
