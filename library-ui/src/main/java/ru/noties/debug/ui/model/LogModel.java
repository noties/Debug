package ru.noties.debug.ui.model;

import ru.noties.storm.Storm;
import ru.noties.storm.exc.StormException;

/**
 * Created by Dimitry Ivanov on 26.06.2015.
 */
public class LogModel {

    private final DatabaseProvider mProvider;

    public LogModel(DatabaseProvider provider) {
        mProvider = provider;
    }

    public void save(LogItem logItem) {
        try {
            Storm.newInsert(mProvider.open()).insert(logItem);
        } catch (StormException e) {
            e.printStackTrace();
        } finally {
            mProvider.close();
        }
    }
}
