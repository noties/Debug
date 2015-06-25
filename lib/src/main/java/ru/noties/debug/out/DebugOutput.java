package ru.noties.debug.out;

import ru.noties.debug.Level;

/**
 * Created by Dimitry Ivanov on 25.06.2015.
 */
public interface DebugOutput {
    void log(Level level, Throwable throwable, String tag, String message);
    boolean isDebug();
}
