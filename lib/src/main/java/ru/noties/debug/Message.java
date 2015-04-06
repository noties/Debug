package ru.noties.debug;

/**
* Created by Dimitry Ivanov (mail@dimitryivanov.ru) on 05.10.14.
*/
public class Message {

    private final Level level;
    private final String tag;
    private final String message;
    private final Throwable throwable;

    Message(Level level, Throwable throwable, String tag, String message) {
        this.level      = level;
        this.throwable  = throwable;
        this.tag        = tag;
        this.message    = message;
    }

    public Level getLevel() {
        return level;
    }

    public String getTag() {
        return tag;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
