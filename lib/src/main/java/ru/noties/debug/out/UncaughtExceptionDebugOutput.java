package ru.noties.debug.out;

import ru.noties.debug.Debug;
import ru.noties.debug.Level;

/**
 * Created by Dimitry Ivanov on 26.06.2015.
 */
public class UncaughtExceptionDebugOutput implements DebugOutput {

    private static final String DEFAULT_OUT_PATTERN = "Uncaught exception, thread: %s";

    public UncaughtExceptionDebugOutput() {
        new UncaughtExceptionHandler();
    }

    @Override
    public void log(Level level, Throwable throwable, String tag, String message) {
        // do nothing
    }

    @Override
    public boolean isDebug() {
        return false;
    }

    private static class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        private final Thread.UncaughtExceptionHandler mDefaultHandler;

        public UncaughtExceptionHandler() {
            this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(this);
        }

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            Debug.e(ex, DEFAULT_OUT_PATTERN, thread, ex);
            mDefaultHandler.uncaughtException(thread, ex);
        }
    }
}
