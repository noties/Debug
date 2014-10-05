package ru.noties.debug.sample;

import android.app.Application;

import ru.noties.debug.Debug;

/**
 * Created by dimaster on 05.10.14.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Debug.init(BuildConfig.DEBUG);
    }
}
