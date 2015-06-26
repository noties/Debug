package ru.noties.debug.ui.model;

import android.content.Context;
import android.net.Uri;

import ru.noties.storm.DatabaseManager;
import ru.noties.storm.Query;
import ru.noties.storm.Storm;
import ru.noties.storm.StormIterator;
import ru.noties.storm.loader.AbsStormSupportLoader;
import ru.noties.storm.query.Selection;

/**
 * Created by Dimitry Ivanov on 26.06.2015.
 */
public class LogLoaderCompat extends AbsStormSupportLoader<StormIterator<LogItem>> {

    private StormIterator<LogItem> mValue;
    private LogItemFilter mFilter;

    public LogLoaderCompat(Context context, DatabaseManager manager) {
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
        if (mFilter == null) {
            mValue = Storm.newSelect(getManager()).queryAllIterator(LogItem.class);
        } else {
            final Selection selection = FilterSelectionConverter.convert(mFilter);
            final Query query = new Query(LogItem.class)
                    .selection(selection);
            mValue = Storm.newSelect(getManager()).queryIterator(query);
        }
        return mValue;
    }

    @Override
    protected Uri getNotificationUri() {
        return getManager().getNotificationUri(LogItem.class);
    }

    public void setLogFilter(LogItemFilter filter) {
        mFilter = filter;
        forceLoad();
    }
}
