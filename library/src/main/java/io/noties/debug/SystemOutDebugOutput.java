package io.noties.debug;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressWarnings("WeakerAccess")
public class SystemOutDebugOutput implements DebugOutput {

    private final boolean isDebug;
    private final DateFormat dateFormat;

    public SystemOutDebugOutput(boolean debug) {
        this(debug, "MM-dd HH:mm:ss.SSS");
    }

    public SystemOutDebugOutput(boolean debug, String dateFormatPattern) {
        this(debug, new SimpleDateFormat(dateFormatPattern, Locale.US));
    }

    public SystemOutDebugOutput(boolean debug, DateFormat dateFormat) {
        this.isDebug = debug;
        this.dateFormat = dateFormat;
    }

    @Override
    public void log(
            @NonNull Level level,
            @Nullable Throwable throwable,
            @NonNull String tag,
            @Nullable String message) {

        final PrintStream out;

        switch (level) {

            case WTF:
            case E:
                out = System.err;
                break;

            default:
                out = System.out;
        }

        if (message == null) {
            message = "";
        }

        out.print(message(level, tag, message));
        out.println();

        if (throwable != null) {
            throwable.printStackTrace(out);
        }
    }

    private String message(@NonNull Level level, @NonNull String tag, @Nullable String message) {
        final String out;
        final String date = dateFormat.format(new Date());
        if (message == null || message.length() == 0) {
            out = String.format("%s %s/ %s", date, level.name(), tag);
        } else {
            out = String.format("%s %s/ %s : %s", date, level.name(), tag, message);
        }
        return out;
    }

    @Override
    public boolean isDebug() {
        return isDebug;
    }
}
