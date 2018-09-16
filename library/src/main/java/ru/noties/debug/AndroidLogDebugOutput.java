package ru.noties.debug;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

public class AndroidLogDebugOutput implements DebugOutput {

    @NonNull
    public static AndroidLogDebugOutput create(boolean isDebug) {
        return new AndroidLogDebugOutput(isDebug);
    }

    private static final int MAX_LENGTH = 4000;

    private final boolean isDebug;

    public AndroidLogDebugOutput(boolean isDebug) {
        this.isDebug = isDebug;
    }

    @Override
    public void log(
            @NonNull Level level,
            @Nullable Throwable throwable,
            @NonNull String tag,
            @Nullable String message) {

        if (throwable != null) {
            final String trace = throwableStackTrace(throwable);
            if (TextUtils.isEmpty(message)) {
                message = trace;
            } else {
                message = message + "\n" + trace;
            }
        }

        final int length = message != null
                ? message.length()
                : 0;

        if (length == 0) {
            log(level, tag, " ");
        } else if (length < MAX_LENGTH) {
            log(level, tag, message);
        } else {
            int start = 0;
            int end = MAX_LENGTH;
            while (end <= length) {
                log(level, tag, message.substring(start, end));
                if (end == length) {
                    break;
                } else {
                    start = end;
                    end = start + Math.min(MAX_LENGTH, (length - start));
                }
            }
        }
    }

    @Override
    public boolean isDebug() {
        return isDebug;
    }

    @NonNull
    private static String throwableStackTrace(Throwable throwable) {
        final StringWriter writer = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        return writer.toString();
    }

    private static void log(@NonNull Level level, @NonNull String tag, @NonNull String message) {
        if (Level.WTF == level) {
            Log.wtf(tag, message);
        } else {
            Log.println(7 - level.ordinal(), tag, message);
        }
    }
}
