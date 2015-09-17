package ru.noties.debug.render;

import java.util.List;

import ru.noties.debug.timer.TimerItem;
import ru.noties.debug.timer.TimerType;

/**
 * Created by Dimitry Ivanov on 16.09.2015.
 */
public interface DebugRender {

    LogItem log     (StackTraceElement element, String message, Object... args);
    LogItem trace   (StackTraceElement[] elements, int maxItems);
    LogItem timer   (StackTraceElement element, String timerName, TimerType timerType, List<TimerItem> timerItems);

}
