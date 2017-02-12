package ru.noties.debug;

import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SystemOutDebugOutput implements DebugOutput {

    private final boolean mIsDebug;
    private final DateFormat mDateFormat;

    public SystemOutDebugOutput(boolean debug) {
        this(debug, "MM-dd HH:mm:ss.SSS");
    }

    public SystemOutDebugOutput(boolean debug, String dateFormatPattern) {
        this(debug, new SimpleDateFormat(dateFormatPattern, Locale.US));
    }

    public SystemOutDebugOutput(boolean debug, DateFormat dateFormat) {
        mIsDebug = debug;
        mDateFormat = dateFormat;
    }

    @Override
    public void log(Level level, Throwable throwable, String tag, String message) {

        final PrintStream out;

        switch (level) {

            case WTF:
            case E:
                out = System.err;
                break;

            default:
                out = System.out;
        }

        out.print(message(level, tag, message));
        out.println();

        if (throwable != null) {
            throwable.printStackTrace(out);
        }
    }

    private String message(Level level, String tag, String message) {
        final String out;
        final String date = mDateFormat.format(new Date());
        if (message == null || message.length() == 0) {
            out = String.format("%s %s/ %s", date, level.name(), tag);
        } else {
            out = String.format("%s %s/ %s : %s", date, level.name(), tag, message);
        }
        return out;
    }

    @Override
    public boolean isDebug() {
        return mIsDebug;
    }
}
