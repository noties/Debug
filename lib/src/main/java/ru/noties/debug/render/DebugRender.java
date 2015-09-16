package ru.noties.debug.render;

/**
 * Created by Dimitry Ivanov on 16.09.2015.
 */
public interface DebugRender {

    LogItem log     (StackTraceElement[] elements, String message, Object... args);
    LogItem trace   (StackTraceElement[] elements, int maxItems);

}
