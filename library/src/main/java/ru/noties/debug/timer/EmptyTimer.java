package ru.noties.debug.timer;

import java.util.List;

/**
 * Created by Dimitry Ivanov (mail@dimitryivanov.ru) on 30.03.2015.
 */
public class EmptyTimer implements Timer {

    @Override
    public void tick() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void tick(String message, Object... args) {

    }

    @Override
    public void start(String message, Object... args) {

    }

    @Override
    public void stop(String message, Object... args) {

    }

    @Override
    public TimerType getTimerType() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<TimerItem> getItems() {
        return null;
    }
}
