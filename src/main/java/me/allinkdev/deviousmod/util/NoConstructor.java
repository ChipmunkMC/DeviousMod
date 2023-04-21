package me.allinkdev.deviousmod.util;

public abstract class NoConstructor {
    protected NoConstructor() {
        throw new IllegalStateException("Class does not have a constructor!");
    }
}
