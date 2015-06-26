package ru.noties.debug.ui.model;

import android.content.Context;

import java.util.concurrent.atomic.AtomicInteger;

import ru.noties.debug.Level;
import ru.noties.storm.DatabaseManager;
import ru.noties.storm.Storm;
import ru.noties.storm.sd.EnumSerializer;

/**
 * Created by Dimitry Ivanov on 26.06.2015.
 */
public class LogDatabase implements DatabaseProvider {

    private static volatile LogDatabase sInstance = null;

    public static LogDatabase getInstance() {
        LogDatabase local = sInstance;
        if (local == null) {
            synchronized (LogDatabase.class) {
                local = sInstance;
                if (local == null) {
                    local = sInstance = new LogDatabase();
                }
            }
        }
        return local;
    }

    private static final String DB_NAME = "debug_logs";
    private static final int DB_VERSION = 1;

    private final AtomicInteger mOpenCount = new AtomicInteger();

    private DatabaseManager mManager;

    private LogDatabase() {}

    public void init(Context context) {
        Storm.getInstance().init(context, true);
        Storm.getInstance().registerTypeSerializer(Level.class, new EnumSerializer<Level>(Level.values()));
        mManager = new DatabaseManager(context, DB_NAME, DB_VERSION, new Class[] { LogItem.class });
    }

    @Override
    public synchronized DatabaseManager open() {
        if (mOpenCount.incrementAndGet() == 1) {
            if (!mManager.isOpen()) {
                mManager.open();
            }
        }
        return mManager;
    }

    @Override
    public synchronized void close() {
        if (mOpenCount.decrementAndGet() == 0) {
            mManager.close();
        }
    }
}
