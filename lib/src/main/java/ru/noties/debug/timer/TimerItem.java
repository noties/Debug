package ru.noties.debug.timer;

/**
 * Created by Dimitry Ivanov on 17.09.2015.
 */
public class TimerItem {

    public enum Type {
        START, TICK, STOP
    }

    public final Type type;
    public final long when;
    public final String message;
    public final Object[] args;

    public TimerItem(Type type, long when, String message, Object[] args) {
        this.type = type;
        this.when = when;
        this.message = message;
        this.args = args;
    }
}
