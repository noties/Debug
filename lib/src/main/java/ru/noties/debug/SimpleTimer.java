package ru.noties.debug;

import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dimitry Ivanov (mail@dimitryivanov.ru) on 05.10.14.
 */
class SimpleTimer implements Timer {

    private static final String TIMER = "timer\n";
    private static final String MILLIS_PATTERN = "%d ms";
    private static final String NANO_PATTERN = "%d ns";
    private static final String TOOK_PATTEN = "took: %s";

    static SimpleTimer newInstance(String name) {
        return newInstance(name, TimerType.MILLIS);
    }

    static SimpleTimer newInstance(String name, TimerType type) {
        return new SimpleTimer(name, type);
    }

    private final String mName;
    private final TimerType mType;

    private final List<Holder> mList;

    private SimpleTimer(String name, TimerType type) {
        this.mName = name;
        this.mType  = type;
        this.mList  = new ArrayList<Holder>();
    }

    @Override
    public void tick() {
        tick(null);
    }

    @Override
    public void start() {
        start(null);
    }

    @Override
    public void stop() {
        stop(null);
    }

    @Override
    public void tick(String message, Object... args) {
        mList.add(new Holder(
                Type.TICK,
                getWhen(),
                message,
                args
        ));
    }

    @Override
    public void start(String message, Object... args) {
        mList.add(new Holder(
                Type.START,
                getWhen(),
                message,
                args
        ));
    }

    @Override
    public void stop(String message, Object... args) {
        mList.add(new Holder(
                Type.STOP,
                getWhen(),
                message,
                args
        ));
    }

    @Override
    public String toString() {
        final StringBuilder builder
                = new StringBuilder(TIMER);
        builder.append(mName);

        if (mList.size() == 0) {
            return builder.toString();
        }

        final long start = mList.get(0).when;
        long last = start;
        String msg;

        for (Holder holder: mList) {

            builder.append("\n")
                    .append(holder.type.value)
                    .append(getWhenString(last, holder.when));
            msg = getMessage(holder.message, holder.args);
            if (msg != null) {
                builder.append(", ")
                        .append(msg);
            }

            last = holder.when;

            if (holder.type == Type.STOP) {
                builder.append("\n")
                        .append(String.format(TOOK_PATTEN, getWhenString(start, last)));
            }
        }

        return builder.toString();
    }

    private String getMessage(String pattern, Object[] args) {
        return Debug.getLogMessage(pattern, args);
    }

    private String getWhenString(long start, long now) {

        final String pattern;

        if (mType == TimerType.NANO) {
            pattern = NANO_PATTERN;
        } else {
            pattern = MILLIS_PATTERN;
        }

        return String.format(pattern, (now - start));
    }

    private long getWhen() {
        if (mType == TimerType.NANO) {
            return System.nanoTime();
        }

        return SystemClock.elapsedRealtime();
    }

    private static class Holder {

        final Type type;
        final long when;
        final String message;
        final Object[] args;

        Holder(Type type, long when, String message, Object[] args) {
            this.type       = type;
            this.when       = when;
            this.message    = message;
            this.args       = args;
        }
    }

    private enum Type {

        TICK    ("\t+ "),
        START   ("start, "),
        STOP    ("stop, ");

        final String value;

        Type(String value) {
            this.value = value;
        }
    }
}
