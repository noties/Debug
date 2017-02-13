package ru.noties.debug;

import java.util.Collection;
import java.util.Locale;
import java.util.regex.Pattern;

@SuppressWarnings("WeakerAccess")
public class Debug {

    private static final Debug INSTANCE = new Debug();
    private static final Pattern STRING_FORMAT_PATTERN = Pattern.compile("%(\\d+\\$)?([-#+ 0,(<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])");

    private static final String FILE_NAME = "Debug.java";
    private static final String STARTING_MESSAGE_PATTERN_LINK = "%1$s(%2$s:%3$d)";
    private static final String TRACE_FIRST_LINE = "trace:\n";

    private DebugOutput output;

    private Debug() {}


    // can be empty, but there will be no logging
    public static void init(DebugOutput... outputs) {

        final DebugOutput out;
        final int length = outputs != null
                ? outputs.length
                : 0;

        if (length == 0) {
            out = null;
        } else {
            if (length == 1) {
                out = outputs[0];
            } else {
                out = new DebugOutputContainer(outputs);
            }
        }

        INSTANCE.output = out;
    }

    public static void init(Collection<? extends DebugOutput> outputs) {

        final DebugOutput out;

        final int length = outputs != null
                ? outputs.size()
                : 0;

        if (length == 0) {
            out = null;
        } else {
            if (length == 1) {
                out = outputs.iterator().next();
            } else {
                final DebugOutput[] debugOutputs = new DebugOutput[length];
                outputs.toArray(debugOutputs);
                out = new DebugOutputContainer(debugOutputs);
            }
        }

        INSTANCE.output = out;
    }

    public static boolean isDebug() {
        final DebugOutput output = INSTANCE.output;
        return output != null && output.isDebug();
    }


    public static void v(Throwable throwable, Object... args) {
        log(Level.V, throwable, args);
    }

    public static void v(Object... args) {
        log(Level.V, null, args);
    }


    public static void d(Throwable throwable, Object... args) {
        log(Level.D, throwable, args);
    }

    public static void d(Object... args) {
        log(Level.D, null, args);
    }


    public static void i(Throwable throwable, Object... args) {
        log(Level.I, throwable, args);
    }

    public static void i(Object... args) {
        log(Level.I, null, args);
    }


    public static void w(Throwable throwable, Object... args) {
        log(Level.W, throwable, args);
    }

    public static void w(Object... args) {
        log(Level.W, null, args);
    }


    public static void e(Throwable throwable, Object... args) {
        log(Level.E, throwable, args);
    }

    public static void e(Object... args) {
        log(Level.E, null, args);
    }


    public static void wtf(Throwable throwable, Object... args) {
        log(Level.WTF, throwable, args);
    }

    public static void wtf(Object... args) {
        log(Level.WTF, null, args);
    }


    public static void trace() {
        trace(Level.V, 0);
    }

    public static void trace(Level level) {
        trace(level, 0);
    }

    public static void trace(int maxItems) {
        trace(Level.V, maxItems);
    }

    public static void trace(Level level, int maxItems) {

        final DebugOutput output = INSTANCE.output;
        if (output == null
                || !output.isDebug()) {
            return;
        }

        final String tag;
        final String message;

        final StackTraceElement[] elements = obtainStackTrace();
        if (elements == null
                || elements.length == 0) {
            tag = null;
            message = null;
        } else {
            tag = callerTag(elements[0]);
            message = traceLogMessage(elements, maxItems);
        }

        output.log(level, null, tag, message);
    }


    private static void log(Level level, Throwable throwable, Object[] args) {

        final DebugOutput output = INSTANCE.output;
        if (output == null
                || !output.isDebug()) {
            return;
        }

        output.log(level, throwable, callerTag(), logMessage(args));
    }

    // can be null
    private static String callerTag() {

        final String out;

        final StackTraceElement[] elements = new Throwable().getStackTrace();
        final int length = elements != null
                ? elements.length
                : 0;

        if (length == 0) {
            out = null;
        } else {

            String elementName;
            int first = -1;

            for (int i = 0; i < length; i++) {

                elementName = elements[i].getFileName();
                if (FILE_NAME.equals(elementName)) {
                    continue;
                }

                first = i;
                break;
            }

            if (first > -1) {
                out = callerTag(elements[first]);
            } else {
                out = null;
            }
        }

        return out;
    }

    private static String callerTag(StackTraceElement element) {
        return String.format(
                Locale.US,
                STARTING_MESSAGE_PATTERN_LINK,
                element.getMethodName(),
                element.getFileName(),
                element.getLineNumber()
        );
    }

    private static String logMessage(Object[] args) {

        final String out;

        final int length = args != null
                ? args.length
                : 0;

        if (length == 0) {
            // nothing here
            out = null;
        } else {
            if (length == 1) {
                // we could insert here an array check, but... it just clutters the code
                // plus we will need to insert a lot of checks for multidimensional array etc.
                // Plus, it's weird that we will do this kind of check only for the first element
                // anyways calling `Arrays.toString` is way better (as caller knows the type of an array at least)
                out = String.valueOf(args[0]);
            } else {

                // if first argument is String -> check if it's a String.format pattern
                // if not, do treat first String argument just as simple Object

                Object first = args[0];
                if (first != null
                        && String.class.equals(first.getClass())) {

                    final String pattern = (String) first;
                    if (STRING_FORMAT_PATTERN.matcher(pattern).find()) {
                        final Object[] formatArgs = new Object[length - 1];
                        System.arraycopy(args, 1, formatArgs, 0, length - 1);
                        out = String.format(pattern, formatArgs);
                    } else {
                        out = concat(args);
                    }
                } else {
                    out = concat(args);
                }
            }
        }

        return out;
    }

    private static String concat(Object[] args) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0, length = args.length; i < length; i++) {
            if (i != 0) {
                builder.append(", ");
            }
            builder.append(args[i]);
        }
        return builder.toString();
    }

    private static StackTraceElement[] obtainStackTrace() {

        final StackTraceElement[] out;

        final StackTraceElement[] elements = new Throwable().getStackTrace();
        final int length = elements != null
                ? elements.length
                : 0;

        if (length == 0) {
            out = null;
        } else {

            String elementName;
            int start = -1;

            for (int i = 0; i < length; i++) {

                elementName = elements[i].getFileName();
                if (FILE_NAME.equals(elementName)) {
                    continue;
                }

                start = i;
                break;
            }

            if (start > -1) {
                final int newLength = length - start;
                out = new StackTraceElement[newLength];
                System.arraycopy(elements, start, out, 0, newLength);
            } else {
                // whatever we have
                out = elements;
            }
        }

        return out;
    }

    private static String traceLogMessage(StackTraceElement[] elements, int maxItems) {

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

        return builder.toString();
    }
}