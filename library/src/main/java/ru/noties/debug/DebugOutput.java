package ru.noties.debug;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface DebugOutput {

    void log(
            @NonNull Level level,
            @Nullable Throwable throwable,
            @NonNull String tag,
            @Nullable String message
    );

    boolean isDebug();
}
