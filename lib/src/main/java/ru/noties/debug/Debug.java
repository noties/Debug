package ru.noties.debug;

import ru.noties.debug.out.AndroidLogDebugOutput;
import ru.noties.debug.out.DebugOutput;

public class Debug {

    private static volatile Debug sInstance = null;

    private static Debug getInstance() {
        Debug local = sInstance;
        if (local == null) {
            synchronized (Debug.class) {
                local = sInstance;
                if (local == null) {
                    local = sInstance = new Debug();
                }
            }
        }
        return local;
    }

    private static final String THIS_FILE_NAME = "Debug.java";
    private static final String STARTING_MESSAGE_PATTERN_LINK = "%1$s(%2$s:%3$d)";
    private static final String TRACE_FIRST_LINE = "trace:\n";

    private DebugOutput output;
    
    private Debug() {}

    @Deprecated
    public static void init(final boolean isDebug) {
        Debug.getInstance().setOutput(new AndroidLogDebugOutput(isDebug));
    }
    
    public static void init(DebugOutput debugOutput) {
        Debug.getInstance().setOutput(debugOutput);
    }

    public void setOutput(DebugOutput output) {
        this.output = output;
    }
    
    public static boolean isDebug() {
        final DebugOutput output = Debug.getInstance().output;
        return output != null && output.isDebug();
    }

    public static Timer newTimer(String name, TimerType type) {
        if (!isDebug()) {
            return new EmptyTimer();
        }
        return SimpleTimer.newInstance(name, type);
    }

    public static Timer newTimer(String name) {
        if (!isDebug()) {
            return new EmptyTimer();
        }
        return SimpleTimer.newInstance(name);
    }

    public static void trace() {
        trace(Level.V, -1);
    }

    public static void trace(Level level) {
        trace(level, -1);
    }

    public static void trace(int maxItems) {
        trace(Level.V, maxItems);
    }

    public static void trace(Level level, int maxItems) {

        if (!isDebug()) return;

        final Throwable throwable = new Throwable();
        final StringBuilder builder = new StringBuilder(TRACE_FIRST_LINE);

        final StackTraceElement[] elements = throwable.getStackTrace();

        String fileName;
        String callerTag = null;

        int items = 0;

        for (StackTraceElement element: elements) {

            fileName = element.getFileName();

            if (THIS_FILE_NAME.equals(fileName)) {
                continue;
            }

            if (callerTag == null) {
                callerTag = fileName;
            }

            if (maxItems > 0 && ++items > maxItems) {
                break;
            }

            builder.append("\tat ")
                    .append(element.toString())
                    .append('\n');
        }

        logTrace(level, new Holder(callerTag, builder.toString()));
    }

    public static void e(Throwable throwable, String message, Object... args) {
        log(Level.E, throwable, message, args);
    }

    public static void e(String message,  Object... args) {
        log(Level.E, null, message, args);
    }

    public static void e() {
        log(Level.E, null, null);
    }

    public static void e(Throwable throwable) {
        log(Level.E, throwable, null);
    }

    public static void e(Object o) {
        log(Level.E, null, String.valueOf(o));
    }

    public static void w(Throwable throwable, String message, Object... args) {
        log(Level.W, throwable, message, args);
    }

    public static void w(String message, Object... args) {
        log(Level.W, null, message, args);
    }

    public static void w() {
        log(Level.W, null, null);
    }

    public static void w(Object o) {
        log(Level.W, null, String.valueOf(o));
    }

    public static void i(Throwable throwable, String message, Object... args) {
        log(Level.I, throwable, message, args);
    }

    public static void i(String message, Object... args) {
        log(Level.I, null, message, args);
    }

    public static void i() {
        log(Level.I, null, null);
    }

    public static void i(Object o) {
        log(Level.I, null, String.valueOf(o));
    }

    public static void d(Throwable throwable, String message, Object... args) {
        log(Level.D, throwable, message, args);
    }

    public static void d(String message, Object... args) {
        log(Level.D, null, message, args);
    }

    public static void d() {
        log(Level.D, null, null);
    }

    public static void d(Object o) {
        log(Level.D, null, String.valueOf(o));
    }

    public static void v(Throwable throwable, String message, Object... args) {
        log(Level.V, throwable, message, args);
    }

    public static void v(String message, Object... args) {
        log(Level.V, null, message, args);
    }

    public static void v() {
        log(Level.V, null, null);
    }

    public static void v(Object o) {
        log(Level.V, null, String.valueOf(o));
    }

    public static void wtf() {
        log(Level.WTF, null, null);
    }

    public static void wtf(String message, Object... args) {
        log(Level.WTF, null, message, args);
    }

    public static void wtf(Throwable throwable, String message, Object... args) {
        log(Level.WTF, throwable, message, args);
    }

    public static void wtf(Object o) {
        log(Level.WTF, null, String.valueOf(o));
    }

    static String getLogMessage(String message, Object... args) {
        if (message == null) {
            return null;
        }

        if (args == null || args.length == 0) {
            return message;
        }

        return String.format(message, args);
    }

    private static Holder getHolder(String message, Object... args) {

        final StackTraceElement[] elements = new Throwable().getStackTrace();

        String fileName;
        String methodName;

        int lineNumber;

        for(StackTraceElement element: elements) {

            fileName = element.getFileName();

            if(THIS_FILE_NAME.equals(fileName)) {
                continue;
            }

            lineNumber = element.getLineNumber();
            methodName = element.getMethodName();

            final String startingMessage
                    = String.format(STARTING_MESSAGE_PATTERN_LINK, methodName, fileName,  lineNumber);
            final String logMessage = getLogMessage(message, args);
            final String out;
            if (logMessage != null) {
                out = startingMessage + " : " + logMessage;
            } else {
                out = startingMessage;
            }

            return new Holder(fileName, out);
        }

        return null;
    }

    private static void log(
            final Level level,
            final Throwable throwable,
            final String message,
            final Object... args
    ) {

        final Debug debug = Debug.getInstance();
        final DebugOutput output = debug.output;
        if (output == null
                || !output.isDebug()) {
            return;
        }

        final Holder holder = getHolder(message, args);
        if (holder != null) {
            output.log(level, throwable, holder.tag, holder.message);
        }
    }

    private static void logTrace(
            Level level,
            Holder holder
    ) {
        final Debug debug = Debug.getInstance();
        final DebugOutput output = debug.output;
        if (output == null
                || !output.isDebug()) {
            return;
        }

        output.log(level, null, holder.tag, holder.message);
    }

    private static class Holder {

        final String tag;
        final String message;

        Holder(String tag, String message) {
            this.tag     = tag;
            this.message = message;
        }
    }
}