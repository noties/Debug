package ru.noties.debug.ui.model;

import ru.noties.debug.Level;

/**
 * Created by Dimitry Ivanov on 26.06.2015.
 */
public class LogItemFilter {

    private Level level;
    private String tag;

    public Level getLevel() {
        return level;
    }

    public LogItemFilter setLevel(Level level) {
        this.level = level;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public LogItemFilter setTag(String tag) {
        this.tag = tag;
        return this;
    }
}
