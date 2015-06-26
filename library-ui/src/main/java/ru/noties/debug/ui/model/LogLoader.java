package ru.noties.debug.ui.model;

import android.content.Context;
import android.net.Uri;

import ru.noties.storm.DatabaseManager;
import ru.noties.storm.Storm;
import ru.noties.storm.StormIterator;
import ru.noties.storm.loader.AbsStormLoader;

/**
 * Created by Dimitry Ivanov on 26.06.2015.
 */
public class LogLoader extends AbsStormLoader<StormIterator<LogItem>> {

    private StormIterator<LogItem> mValue;

    public LogLoader(Context context, DatabaseManager manager) {
        super(context, manager);
    }

    @Override
    public void onReset() {
        super.onReset();

        if (mValue != null) {
            mValue.close();
        }

        LogDatabase.getInstance().close();
    }

    @Override
    protected StormIterator<LogItem> loadValue() {
        mValue = Storm.newSelect(getManager()).queryAllIterator(LogItem.class);
        return mValue;
    }

    @Override
    protected Uri getNotificationUri() {
        return getManager().getNotificationUri(LogItem.class);
    }
}
