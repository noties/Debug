package ru.noties.debug;

import android.util.Log;

public class Debug {

    private static final String THIS_FILE_NAME = "Debug.java";
    private static final String STARTING_MESSAGE_PATTERN = "%1$s : %2$s() : %3$d";
    private static final String MESSAGE_PATTERN = "%1$s : %2$s";
    private static final String EXCEPTION_PATTERN = "Exception: %1$s";

    private static boolean isDebug;
    private static String sTag;

    private Debug() {

    }

    public static void init(Configuration configuration) {
        sTag            = configuration.getTag();
        isDebug         = configuration.isDebug();
    }

    private static String getMessage(String message, Object... args) {

        if (message == null) {
            message = "";
        }

        if(args.length == 0) {
            return String.format(
                    MESSAGE_PATTERN,
                    getStartingMessage(),
                    message
            );
        }

        return String.format(
                MESSAGE_PATTERN,
                getStartingMessage(),
                String.format(message, args)
        );
    }

    private static String getStartingMessage() {

        StackTraceElement[] elements = new Throwable().getStackTrace();

        String fileName;
        int lineNumber;
        String methodName;

        for(StackTraceElement element: elements) {

            fileName = element.getFileName();

            if(THIS_FILE_NAME.equals(fileName)) {
                continue;
            }

            lineNumber = element.getLineNumber();
            methodName = element.getMethodName();

            return String.format(STARTING_MESSAGE_PATTERN, fileName, methodName, lineNumber);
        }
        return "";
    }

    public static void trace() {

        if (!isDebug) return;

        StackTraceElement[] elements = new Throwable().getStackTrace();

        String fileName;
        String methodName;
        int lineNumber;

        StringBuilder builder = new StringBuilder();

        for (StackTraceElement element: elements) {

            fileName = element.getFileName();

            if( THIS_FILE_NAME.equals(fileName)) {
                continue;
            }

            methodName = element.getMethodName();
            lineNumber = element.getLineNumber();

            builder.append(String.format(STARTING_MESSAGE_PATTERN, fileName, methodName, lineNumber))
                    .append("\n");
        }

        Log.v(sTag, builder.toString());
    }

    public static void e(Throwable throwable, String message, Object... args) {
        if (!isDebug) return;
        Log.e(sTag, getMessage(message, args), throwable);
    }

    public static void e(String message,  Object... args) {
        e(null, message, args);
    }

    public static void e() {
        e(null);
    }

    public static void e(Throwable throwable) {
        if (!isDebug) return;
        e(throwable, EXCEPTION_PATTERN, throwable);
    }

    public static void w(Throwable throwable, String message, Object... args) {
        if (!isDebug) return;
        Log.w(sTag, getMessage(message, args), throwable);
    }

    public static void w(String message, Object... args) {
        w(null, message, args);
    }

    public static void w() {
        w(null);
    }

    public static void i(Throwable throwable, String message, Object... args) {
        if(!isDebug) return;
        Log.i(sTag, getMessage(message, args), throwable);
    }

    public static void i(String message, Object... args) {
        i(null, message, args);
    }

    public static void i() {
        i(null);
    }

    public static void d(Throwable throwable, String message, Object... args) {
        if(!isDebug) return;
        Log.d(sTag, getMessage(message, args), throwable);
    }

    public static void d(String message, Object... args) {
        d(null, message, args);
    }

    public static void d() {
        d(null);
    }

    public static void v(Throwable throwable, String message, Object... args) {
        if(!isDebug) return;
        Log.v(sTag, getMessage(message, args), throwable);
    }

    public static void v(String message, Object... args) {
        v(null, message, args);
    }

    public static void v() {
        v(null);
    }
}