package ru.noties.debug;

public interface DebugOutput {

    void log(
            /*Nonnull*/ Level level,
            /*Nullable*/ Throwable throwable,
            /*Nullable*/ String tag,
            /*Nullable*/ String message
    );

    boolean isDebug();
}
