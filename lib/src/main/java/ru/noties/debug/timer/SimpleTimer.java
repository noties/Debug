package ru.noties.debug.timer;

import android.os.SystemClock;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dimitry Ivanov (mail@dimitryivanov.ru) on 05.10.14.
 */
public class SimpleTimer implements Timer {

    public static SimpleTimer newInstance(String name) {
        return newInstance(name, TimerType.MILLIS);
    }

    public static SimpleTimer newInstance(String name, TimerType type) {
        return new SimpleTimer(name, type);
    }

    private final String mName;
    private final TimerType mType;

    private final List<TimerItem> mList;

    private SimpleTimer(String name, TimerType type) {
        this.mName = name;
        this.mType  = type;
        this.mList  = new ArrayList<TimerItem>();
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
        mList.add(new TimerItem(
                TimerItem.Type.TICK,
                getWhen(),
                message,
                args
        ));
    }

    @Override
    public void start(String message, Object... args) {
        mList.add(new TimerItem(
                TimerItem.Type.START,
                getWhen(),
                message,
                args
        ));
    }

    @Override
    public void stop(String message, Object... args) {
        mList.add(new TimerItem(
                TimerItem.Type.STOP,
                getWhen(),
                message,
                args
        ));
    }

    @Override
    public TimerType getTimerType() {
        return mType;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public List<TimerItem> getItems() {
        return mList;
    }

    private long getWhen() {
        if (mType == TimerType.NANO) {
            return System.nanoTime();
        }

        return SystemClock.elapsedRealtime();
    }
}
