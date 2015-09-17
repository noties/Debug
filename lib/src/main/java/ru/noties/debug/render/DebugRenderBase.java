package ru.noties.debug.render;

import java.util.List;

import ru.noties.debug.Debug;
import ru.noties.debug.timer.TimerItem;
import ru.noties.debug.timer.TimerType;

/**
 * Created by Dimitry Ivanov on 16.09.2015.
 */
public class DebugRenderBase implements DebugRender {

    protected static final String STARTING_MESSAGE_PATTERN_LINK = "%1$s(%2$s:%3$d)";

    protected static final String TRACE_FIRST_LINE = "trace:\n";

    protected static final String TIMER = " : timer\n";
    protected static final String MILLIS_PATTERN = "%d ms";
    protected static final String NANO_PATTERN = "%d ns";
    protected static final String TOOK_PATTEN = "took: %s";

    @Override
    public LogItem log(StackTraceElement element, String message, Object... args) {

        final String fileName = element.getFileName();

        final String startingMessage = createStartingMessageWithLink(element);

        final String logMessage = Debug.getLogMessage(message, args);
        final String out;
        if (logMessage != null) {
            out = startingMessage + " : " + logMessage;
        } else {
            out = startingMessage;
        }

        return new LogItem(fileName, out);
    }

    protected static String createStartingMessageWithLink(StackTraceElement element) {
        return String.format(
                STARTING_MESSAGE_PATTERN_LINK,
                element.getMethodName(),
                element.getFileName(),
                element.getLineNumber()
        );
    }

    @Override
    public LogItem trace(StackTraceElement[] elements, int maxItems) {

        final StringBuilder builder = new StringBuilder(TRACE_FIRST_LINE);

        String fileName;
        String callerTag = null;

        int items = 0;

        for (StackTraceElement element: elements) {

            fileName = element.getFileName();

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

        return new LogItem(callerTag, builder.toString());
    }

    @Override
    public LogItem timer(StackTraceElement element, String timerName, TimerType timerType, List<TimerItem> timerItems) {

        final String fileName = element.getFileName();
        final String methodStart = createStartingMessageWithLink(element);

        final StringBuilder builder
                = new StringBuilder(methodStart)
                .append(TIMER)
                .append(timerName);

        final long start = timerItems.get(0).when;
        long last = start;
        String msg;

        for (TimerItem item: timerItems) {

            builder.append("\n")
                    .append(createTimerItemTypeRepr(item.type));
            if (item.type != TimerItem.Type.START) {
                builder.append(createTimerWhen(timerType, last, item.when));
            }
            msg = Debug.getLogMessage(item.message, item.args);
            if (msg != null) {
                builder.append(", ")
                        .append(msg);
            }

            last = item.when;

            if (item.type == TimerItem.Type.STOP) {
                builder.append("\n")
                        .append(String.format(TOOK_PATTEN, createTimerWhen(timerType, start, last)));
            }
        }

        return new LogItem(fileName, builder.toString());
    }

    protected static String createTimerItemTypeRepr(TimerItem.Type type) {

        switch (type) {

            case START:
                return "start";

            case TICK:
                return "\u00a0    + ";

            case STOP:
                return "stop, ";

            default:
                return null;
        }
    }

    protected static String createTimerWhen(TimerType timerType, long last, long current) {

        final String pattern;

        if (timerType == TimerType.NANO) {
            pattern = NANO_PATTERN;
        } else {
            pattern = MILLIS_PATTERN;
        }

        return String.format(pattern, (current - last));
    }
}
