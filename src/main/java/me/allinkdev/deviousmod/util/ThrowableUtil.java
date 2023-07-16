package me.allinkdev.deviousmod.util;

public final class ThrowableUtil extends NoConstructor {
    public static void appendStackTrace(final StackTraceElement[] traceElements, final StringBuilder builder) {
        for (final StackTraceElement traceElement : traceElements) builder.append("\tat ").append(traceElement).append("\n");
    }

    public static String getStackTrace(final StackTraceElement[] elements) {
        final StringBuilder builder = new StringBuilder();
        appendStackTrace(elements, builder);
        return builder.toString();
    }

    public static String getStackTrace(final Throwable ex, final int depth) {
        final StringBuilder builder = new StringBuilder();
        if (depth >= 128) return builder.append("Depth exceeded maximum!").toString();
        builder.append(ex).append("\n");

        final StackTraceElement[] elements = ex.getStackTrace();
        appendStackTrace(elements, builder);

        for (final Throwable se : ex.getSuppressed()) builder.append("\tSuppressed: ").append(getStackTrace(se, depth + 1)).append("\n");
        final Throwable ourCause = ex.getCause();

        if (ourCause != null) builder.append("Caused by:").append(getStackTrace(ourCause, depth + 1)).append("\n");
        return builder.toString();
    }
}
