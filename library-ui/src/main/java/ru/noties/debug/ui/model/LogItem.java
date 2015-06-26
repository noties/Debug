package ru.noties.debug.ui.model;

import ru.noties.debug.Level;
import ru.noties.storm.anno.Autoincrement;
import ru.noties.storm.anno.Column;
import ru.noties.storm.anno.PrimaryKey;
import ru.noties.storm.anno.Table;

/**
 * Created by Dimitry Ivanov on 26.06.2015.
 */
@Table("logs")
public class LogItem {

    public static final String COL_DATE     = "log_date";
    public static final String COL_LEVEL    = "log_level";
    public static final String COL_TAG      = "log_tag";
    public static final String COL_MESSAGE  = "log_message";

    @Column
    @PrimaryKey
    @Autoincrement
    private long id;

    @Column(COL_DATE)
    private long date;

    @Column(COL_LEVEL)
    private Level level;

    @Column(COL_TAG)
    private String tag;

    @Column(COL_MESSAGE)
    private String message;

    public long getId() {
        return id;
    }

    public LogItem setId(long id) {
        this.id = id;
        return this;
    }

    public long getDate() {
        return date;
    }

    public LogItem setDate(long date) {
        this.date = date;
        return this;
    }

    public Level getLevel() {
        return level;
    }

    public LogItem setLevel(Level level) {
        this.level = level;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public LogItem setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public LogItem setMessage(String message) {
        this.message = message;
        return this;
    }
}
