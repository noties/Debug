package ru.noties.debug.render;

import ru.noties.debug.Debug;

/**
 * Created by Dimitry Ivanov on 16.09.2015.
 */
public class DebugRenderImpl implements DebugRender {

    protected static final String STARTING_MESSAGE_PATTERN_LINK = "%1$s(%2$s:%3$d)";
    protected static final String TRACE_FIRST_LINE = "trace:\n";

    @Override
    public LogItem log(StackTraceElement[] elements, String message, Object... args) {

        if (elements.length <= 0) {
            return null;
        }

        final StackTraceElement element = elements[0];
        final String fileName = element.getFileName();
        final String methodName = element.getMethodName();
        final int lineNumber = element.getLineNumber();

        final String startingMessage
                    = String.format(STARTING_MESSAGE_PATTERN_LINK, methodName, fileName,  lineNumber);
        final String logMessage = Debug.getLogMessage(message, args);
        final String out;
        if (logMessage != null) {
            out = startingMessage + " : " + logMessage;
        } else {
            out = startingMessage;
        }

        return new LogItem(fileName, out);
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
}
