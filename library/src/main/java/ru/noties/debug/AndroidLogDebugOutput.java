package ru.noties.debug;

import android.text.TextUtils;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

public class AndroidLogDebugOutput implements DebugOutput {

    private static final int MAX_LENGTH = 4000;

    private final boolean isDebug;

    public AndroidLogDebugOutput(boolean isDebug) {
        this.isDebug = isDebug;
    }
    
    @Override
    public void log(Level level, Throwable throwable, String tag, String message) {

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

        final LogInvoke invoke = logInvoke(level);

        if (length == 0) {
            // cannot print null...
            invoke.invoke(tag, " ");
        } else if (length < MAX_LENGTH) {
            invoke.invoke(tag, message);
        } else {
            int start = 0;
            int end = MAX_LENGTH;
            while (end <= length) {
                invoke.invoke(tag, message.substring(start, end));
                tag = null;
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

    private static String throwableStackTrace(Throwable throwable) {
        final StringWriter writer = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        return writer.toString();
    }

    private interface LogInvoke {
        void invoke(String tag, String message);
    }

    private static LogInvoke logInvoke(Level level) {
        final LogInvoke invoke;
        if (Level.WTF == level) {
            invoke = new LogInvoke() {
                @Override
                public void invoke(String tag, String message) {
                    Log.wtf(tag, message);
                }
            };
        } else {
            final int priority;
            switch (level) {
                case E:
                    priority = Log.ERROR;
                    break;
                case W:
                    priority = Log.WARN;
                    break;
                case I:
                    priority = Log.INFO;
                    break;
                case V:
                    priority = Log.VERBOSE;
                    break;
                default:
                    priority = Log.DEBUG;
                    break;
            }
            invoke = new LogInvoke() {
                @Override
                public void invoke(String tag, String message) {
                    Log.println(priority, tag, message);
                }
            };
        }
        return invoke;
    }
}
