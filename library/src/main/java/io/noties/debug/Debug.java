package io.noties.debug;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Pattern;

import static io.noties.debug.Level.D;
import static io.noties.debug.Level.E;
import static io.noties.debug.Level.I;
import static io.noties.debug.Level.V;
import static io.noties.debug.Level.W;
import static io.noties.debug.Level.WTF;

@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
public class Debug {

    private static final Debug INSTANCE = new Debug();

    private static final Pattern STRING_FORMAT_PATTERN =
            Pattern.compile("%(\\d+\\$)?([-#+ 0,(<]*)?(\\d+)?(\\.\\d+)?([tT])?([a-zA-Z%])");

    private static final String FILE_NAME = "Debug.java";
    private static final String STARTING_MESSAGE_PATTERN_LINK = "%1$s(%2$s:%3$d)";
    private static final String TRACE_FIRST_LINE = "trace:\n";

    private static final String DEFAULT_TAG = "io.noties.Debug";

    private DebugOutput output;

    private Debug() {
    }


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
                out = DebugOutputContainer.create(outputs);
            }
        }

        INSTANCE.output = out;
    }

    public static void init(@NonNull Collection<? extends DebugOutput> outputs) {

        final DebugOutput out;

        final int length = outputs.size();

        if (length == 0) {
            out = null;
        } else {
            if (length == 1) {
                out = outputs.iterator().next();
            } else {
                out = DebugOutputContainer.create(outputs);
            }
        }

        INSTANCE.output = out;
    }

    public static boolean isDebug() {
        final DebugOutput output = INSTANCE.output;
        return output != null && output.isDebug();
    }

    // #############################################################################################
    // VERBOSE

    public static void v() {
        log(V, null, null);
    }

    public static void v(Object o1) {
        log(V, null, o1);
    }

    public static void v(Object... args) {
        log(V, null, args);
    }

    public static void v(Throwable throwable) {
        log(V, throwable, null);
    }

    public static void v(Throwable throwable, Object... args) {
        log(V, throwable, args);
    }

    // #############################################################################################
    // DEBUG

    public static void d() {
        log(D, null, null);
    }

    public static void d(Object o1) {
        log(D, null, o1);
    }

    public static void d(Object... args) {
        log(D, null, args);
    }

    public static void d(Throwable throwable) {
        log(D, throwable, null);
    }

    public static void d(Throwable throwable, Object... args) {
        log(D, throwable, args);
    }

    // #############################################################################################
    // INFO

    public static void i() {
        log(I, null, null);
    }

    public static void i(Object o1) {
        log(I, null, o1);
    }

    public static void i(Object... args) {
        log(I, null, args);
    }

    public static void i(Throwable throwable) {
        log(I, throwable, null);
    }

    public static void i(Throwable throwable, Object... args) {
        log(I, throwable, args);
    }

    // #############################################################################################
    // WARN

    public static void w() {
        log(W, null, null);
    }

    public static void w(Object o1) {
        log(W, null, o1);
    }

    public static void w(Object... args) {
        log(W, null, args);
    }

    public static void w(Throwable throwable) {
        log(W, throwable, null);
    }

    public static void w(Throwable throwable, Object... args) {
        log(W, throwable, args);
    }

    // #############################################################################################
    // ERROR

    public static void e() {
        log(E, null, null);
    }

    public static void e(Object o1) {
        log(E, null, o1);
    }

    public static void e(Object... args) {
        log(E, null, args);
    }

    public static void e(Throwable throwable) {
        log(E, throwable, null);
    }

    public static void e(Throwable throwable, Object... args) {
        log(E, throwable, args);
    }

    // #############################################################################################
    // WTF

    public static void wtf() {
        log(WTF, null, null);
    }

    public static void wtf(Object o1) {
        log(WTF, null, o1);
    }

    public static void wtf(Object... args) {
        log(WTF, null, args);
    }

    public static void wtf(Throwable throwable) {
        log(WTF, throwable, null);
    }

    public static void wtf(Throwable throwable, Object... args) {
        log(WTF, throwable, args);
    }

    // #############################################################################################
    // TRACE

    public static void trace() {
        trace(V, 0);
    }

    public static void trace(Level level) {
        trace(level, 0);
    }

    public static void trace(int maxItems) {
        trace(V, maxItems);
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
            tag = DEFAULT_TAG;
            message = null;
        } else {
            tag = callerTag(elements[0]);
            message = traceLogMessage(elements, maxItems);
        }

        output.log(level, null, tag, message);
    }

    private static void log(@NonNull Level level, @Nullable Throwable throwable, @Nullable Object o1) {

        final DebugOutput output = INSTANCE.output;
        if (output == null
                || !output.isDebug()) {
            return;
        }

        output.log(level, throwable, callerTag(), logMessage(o1));
    }

    private static void log(@NonNull Level level, @Nullable Throwable throwable, Object[] args) {

        final DebugOutput output = INSTANCE.output;
        if (output == null
                || !output.isDebug()) {
            return;
        }

        output.log(level, throwable, callerTag(), logMessage(args));
    }

    @NonNull
    private static String callerTag() {

        final String out;

        final StackTraceElement[] elements = new Throwable().getStackTrace();
        final int length = elements != null
                ? elements.length
                : 0;

        if (length == 0) {
            out = DEFAULT_TAG;
        } else {

            int first = -1;

            for (int i = 0; i < length; i++) {

                if (FILE_NAME.equals(elements[i].getFileName())) {
                    continue;
                }

                first = i;
                break;
            }

            if (first > -1) {
                out = callerTag(elements[first]);
            } else {
                out = DEFAULT_TAG;
            }
        }

        return out;
    }

    @NonNull
    private static String callerTag(@NonNull StackTraceElement element) {
        return String.format(
                Locale.US,
                STARTING_MESSAGE_PATTERN_LINK,
                element.getMethodName(),
                element.getFileName(),
                element.getLineNumber()
        );
    }

    @Nullable
    private static String logMessage(@Nullable Object... args) {

        final String out;

        final int length = args != null
                ? args.length
                : 0;

        if (length == 0) {
            // nothing here
            out = null;
        } else {

            // @since 4.0.0 process array arguments and automatically convert to a string
            processArrays(args, length);

            if (length == 1) {
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
                        out = concat(args, length);
                    }
                } else {
                    out = concat(args, length);
                }
            }
        }

        return out;
    }

    @NonNull
    private static String concat(@NonNull Object[] args, int length) {
        final StringBuilder builder = new StringBuilder();
        builder.append(args[0]);
        for (int i = 1; i < length; i++) {
            builder.append(", ").append(args[i]);
        }
        return builder.toString();
    }

    @Nullable
    private static StackTraceElement[] obtainStackTrace() {

        final StackTraceElement[] out;

        final StackTraceElement[] elements = new Throwable().getStackTrace();
        final int length = elements != null
                ? elements.length
                : 0;

        if (length == 0) {
            out = null;
        } else {

            int start = -1;

            for (int i = 0; i < length; i++) {

                if (FILE_NAME.equals(elements[i].getFileName())) {
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

    @NonNull
    private static String traceLogMessage(@NonNull StackTraceElement[] elements, int maxItems) {

        final StringBuilder builder = new StringBuilder(TRACE_FIRST_LINE);

        String fileName;
        String callerTag = null;

        int items = 0;

        for (StackTraceElement element : elements) {

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

    private static void processArrays(@NonNull Object[] args, int length) {

        if (length == 0) {
            return;
        }

        Object obj;
        Class<?> cl;

        for (int i = 0; i < length; i++) {
            obj = args[i];
            if (obj != null) {
                cl = obj.getClass();
                if (cl.isArray()) {
                    final String replace;
                    if (cl == byte[].class) replace = Arrays.toString((byte[]) obj);
                    else if (cl == short[].class) replace = Arrays.toString((short[]) obj);
                    else if (cl == int[].class) replace = Arrays.toString((int[]) obj);
                    else if (cl == long[].class) replace = Arrays.toString((long[]) obj);
                    else if (cl == char[].class) replace = Arrays.toString((char[]) obj);
                    else if (cl == float[].class) replace = Arrays.toString((float[]) obj);
                    else if (cl == double[].class) replace = Arrays.toString((double[]) obj);
                    else if (cl == boolean[].class) replace = Arrays.toString((boolean[]) obj);
                    else replace = Arrays.deepToString((Object[]) obj);
                    args[i] = replace;
                }
            }
        }
    }
}