package io.noties.debug;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class DebugOutputContainer implements DebugOutput {

    @NonNull
    public static DebugOutputContainer create(DebugOutput... outputs) {
        return new DebugOutputContainer(Arrays.asList(outputs));
    }

    @NonNull
    public static DebugOutputContainer create(@NonNull Collection<? extends DebugOutput> outputs) {
        return new DebugOutputContainer(outputs);
    }

    private final boolean isDebug;
    private final List<DebugOutput> outputs;

    @Deprecated
    public DebugOutputContainer(@NonNull DebugOutput[] outputs) {
        this(Arrays.asList(outputs));
    }

    DebugOutputContainer(@NonNull Collection<? extends DebugOutput> outputs) {
        final List<DebugOutput> list = new ArrayList<>(outputs.size());
        for (DebugOutput output : outputs) {
            if (output != null && output.isDebug()) {
                list.add(output);
            }
        }
        this.isDebug = list.size() > 0;
        this.outputs = isDebug
                ? Collections.unmodifiableList(list)
                : Collections.<DebugOutput>emptyList();
    }

    @Override
    public void log(
            @NonNull Level level,
            @Nullable Throwable throwable,
            @NonNull String tag,
            @Nullable String message) {
        for (DebugOutput output : outputs) {
            output.log(level, throwable, tag, message);
        }
    }

    @Override
    public boolean isDebug() {
        return isDebug;
    }
}
