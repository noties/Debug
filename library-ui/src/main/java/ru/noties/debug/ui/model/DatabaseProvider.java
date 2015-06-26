package ru.noties.debug.ui.model;

import ru.noties.storm.DatabaseManager;

/**
 * Created by Dimitry Ivanov on 26.06.2015.
 */
public interface DatabaseProvider {
    DatabaseManager open();
    void close();
}
