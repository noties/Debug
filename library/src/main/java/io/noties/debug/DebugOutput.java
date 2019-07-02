package io.noties.debug;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface DebugOutput {

    void log(
            @NonNull Level level,
            @Nullable Throwable throwable,
            @NonNull String tag,
            @Nullable String message
    );

    boolean isDebug();
}
