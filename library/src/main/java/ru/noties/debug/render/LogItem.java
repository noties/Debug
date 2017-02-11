package ru.noties.debug.render;

/**
 * Created by Dimitry Ivanov on 16.09.2015.
 */
public class LogItem {

    public final String tag;
    public final String message;

    public LogItem(
            String tag,
            String message
    ) {
        this.tag = tag;
        this.message = message;
    }
}
