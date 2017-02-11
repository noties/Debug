package ru.noties.debug.timer;

import java.util.Arrays;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimerItem timerItem = (TimerItem) o;

        if (when != timerItem.when) return false;
        if (type != timerItem.type) return false;
        if (message != null ? !message.equals(timerItem.message) : timerItem.message != null)
            return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(args, timerItem.args);

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (int) (when ^ (when >>> 32));
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (args != null ? Arrays.hashCode(args) : 0);
        return result;
    }
}
