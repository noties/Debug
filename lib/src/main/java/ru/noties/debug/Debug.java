package ru.noties.debug;

import android.util.Log;

public class Debug {

    private static final String THIS_FILE_NAME = "Debug.java";
    private static final String STARTING_MESSAGE_PATTERN_LINK = "%1$s(%2$s:%3$d) : ";
    private static final String TRACE_FIRST_LINE = "trace:";
    private static final String EXCEPTION_PATTERN = "Exception: %1$s";
    private static final String TRACE_PATTERN = "at %1$s.%2$s(%3$s:%4$d)";

    private static boolean isDebug;

    private Debug() {

    }

    public static void init(final boolean debug) {
        isDebug = debug;
    }

    public static Timer newTimer(String name, TimerType type) {
        return SimpleTimer.newInstance(name, type);
    }

    public static Timer newTimer(String name) {
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

        if (!isDebug) return;

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
                continue;
            }

            builder.append('\n')
                    .append(
                            String.format(
                                    TRACE_PATTERN,
                                    element.getClassName(),
                                    element.getMethodName(),
                                    fileName,
                                    element.getLineNumber()
                            )
                    );
        }

        log(new Message(level, null, callerTag, builder.toString()));
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
        log(Level.E, throwable, EXCEPTION_PATTERN, throwable);
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

    public static void i(Throwable throwable, String message, Object... args) {
        log(Level.I, throwable, message, args);
    }

    public static void i(String message, Object... args) {
        log(Level.I, null, message, args);
    }

    public static void i() {
        log(Level.I, null, null);
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

    public static void v(Throwable throwable, String message, Object... args) {
        log(Level.V, throwable, message, args);
    }

    public static void v(String message, Object... args) {
        log(Level.V, null, message, args);
    }

    public static void v() {
        log(Level.V, null, null);
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

    static String getLogMessage(String message, Object... args) {
        if (message == null) {
            return "";
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

            return new Holder(fileName, startingMessage + logMessage);
        }

        return null;
    }

    private static Message getMessage(
            final Level level,
            final Throwable throwable,
            final String message,
            final Object... args
    ) {

        final Holder holder = getHolder(message, args);
        if (holder == null) {
            return null;
        }

        return new Message(level, throwable, holder.getTag(), holder.getMessage());
    }

    private static void log(
            final Level level,
            final Throwable throwable,
            final String message,
            final Object... args
    ) {

        if (!isDebug) {
            return;
        }

        final Message m = getMessage(level, throwable, message, args);

        log(m);
    }

    private static void log(Message message) {

        if (message == null) {
            return;
        }

        switch (message.getLevel()) {

            case E:
                Log.e(message.getTag(), message.getMessage(), message.getThrowable());
                break;

            case W:
                Log.w(message.getTag(), message.getMessage(), message.getThrowable());
                break;

            case I:
                Log.i(message.getTag(), message.getMessage(), message.getThrowable());
                break;

            case D:
                Log.d(message.getTag(), message.getMessage(), message.getThrowable());
                break;

            case V:
                Log.v(message.getTag(), message.getMessage(), message.getThrowable());
                break;

            default:
                Log.wtf(message.getTag(), message.getMessage(), message.getThrowable());
        }
    }

    private static class Holder {

        private final String tag;
        private final String message;

        private Holder(String tag, String message) {
            this.tag     = tag;
            this.message = message;
        }

        public String getTag() {
            return tag;
        }

        public String getMessage() {
            return message;
        }
    }
}