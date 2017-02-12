package ru.noties.debug.sample;

import android.app.Application;

import ru.noties.debug.AndroidLogDebugOutput;
import ru.noties.debug.Debug;
import ru.noties.debug.DebugRemove;

@DebugRemove(false)
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Debug.init(new AndroidLogDebugOutput(BuildConfig.DEBUG));
    }
}
