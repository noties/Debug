package ru.noties.debug;

import ru.noties.debug.out.DebugOutput;
import ru.noties.debug.render.DebugRender;
import ru.noties.debug.render.DebugRenderImpl;
import ru.noties.debug.render.LogItem;

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

    private static final String FILE_NAME = "Debug.java";

    private DebugOutput output;
    private DebugRender render;
    
    private Debug() {}
    
    public static void init(DebugOutput debugOutput) {
        Debug.init(debugOutput, new DebugRenderImpl());
    }

    public static void init(DebugOutput debugOutput, DebugRender debugRender) {
        final Debug debug = Debug.getInstance();
        debug.output = debugOutput;
        debug.render = debugRender;
    }

    public void setOutput(DebugOutput output) {
        this.output = output;
    }

    public void setRender(DebugRender render) {
        this.render = render;
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

    static void log(Level level, Throwable throwable, String message, Object... args) {

        final Debug debug = Debug.getInstance();
        final DebugOutput output = debug.output;
        final DebugRender render = debug.render;
        if (output == null
                || render == null
                || !output.isDebug()) {
            return;
        }

        final LogItem logItem = render.log(obtainStackTrace(), message, args);
        if (logItem != null) {
            output.log(level, throwable, logItem.tag, logItem.message);
        }
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

        final Debug debug = Debug.getInstance();
        final DebugOutput output = debug.output;
        final DebugRender render = debug.render;
        if (output == null
                || render == null
                || !output.isDebug()) {
            return;
        }

        final LogItem logItem = render.trace(obtainStackTrace(), maxItems);
        if (logItem != null) {
            output.log(level, null, logItem.tag, logItem.message);
        }
    }

    public static String getLogMessage(String message, Object[] args) {
        if (message == null) {
            return null;
        }

        if (args == null || args.length == 0) {
            return message;
        }

        return String.format(message, args);
    }

    private static StackTraceElement[] obtainStackTrace() {

        final StackTraceElement[] elements = new Throwable().getStackTrace();
        final int length = elements.length;
        final String debugFileName = FILE_NAME;

        String elementName;
        int start = -1;

        for (int i = 0; i < length; i++) {

            elementName = elements[i].getFileName();
            if (debugFileName.equals(elementName)) {
                continue;
            }

            start = i;
            break;
        }

        if (start > 0) {
            final int newLength = length - start;
            final StackTraceElement[] out = new StackTraceElement[newLength];
            System.arraycopy(elements, start, out, 0, newLength);
            return out;
        }

        return elements;
    }
}