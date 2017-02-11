package ru.noties.debug.out;

import java.io.PrintStream;

import ru.noties.debug.Level;

/**
 * Created by Dimitry Ivanov on 25.08.2016.
 */
public class SystemOutDebugOutput implements DebugOutput {

    private final boolean mIsDebug;

    public SystemOutDebugOutput(boolean isDebug) {
        mIsDebug = isDebug;
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

    protected String message(Level level, String tag, String message) {
        final String out;
        if (message == null || message.length() == 0) {
            out = String.format("%s/ %s", level.name(), tag);
        } else {
            out = String.format("%s/ %s : %s", level.name(), tag, message);
        }
        return out;
    }

    @Override
    public boolean isDebug() {
        return mIsDebug;
    }
}
