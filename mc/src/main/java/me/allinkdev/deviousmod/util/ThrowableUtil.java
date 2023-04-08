package me.allinkdev.deviousmod.util;

public final class ThrowableUtil {
    ThrowableUtil() {
        throw new UnsupportedOperationException("ThrowableUtil is not to be instantiated!");
    }

    public static String getStackTrace(final Throwable ex, final int depth) {
        final StringBuilder builder = new StringBuilder();

        if (depth >= 128) {
            return builder.append("Depth exceeded maximum!").toString();
        }

        builder.append(ex).append("\n");
        final StackTraceElement[] trace = ex.getStackTrace();
        for (final StackTraceElement traceElement : trace) {
            builder.append("\tat ").append(traceElement).append("\n");
        }

        for (final Throwable se : ex.getSuppressed()) {
            builder.append("\tSuppressed: ").append(getStackTrace(se, depth + 1)).append("\n");
        }

        final Throwable ourCause = ex.getCause();

        if (ourCause != null) {
            builder.append("Caused by:").append(getStackTrace(ourCause, depth + 1)).append("\n");
        }

        return builder.toString();
    }
}
