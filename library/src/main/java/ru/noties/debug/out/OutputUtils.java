package ru.noties.debug.out;

@Deprecated
public class OutputUtils {

    private OutputUtils() {}

    public static String convert(Throwable t) {
        final StackTraceElement[] elements = t.getStackTrace();
        if (elements == null
                || elements.length == 0) {
            return t.toString();
        }

        final StringBuilder builder = new StringBuilder("\n")
                .append(t.toString())
                .append('\n');

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, size = elements.length; i < size; i++) {
            builder.append("\tat ")
                    .append(elements[i].toString())
                    .append('\n');
        }
        return builder.toString();
    }
}
