package ru.noties.debug.remove.test;

import android.app.Application;

import ru.noties.debug.Debug;
import ru.noties.debug.DebugOutput;
import ru.noties.debug.DebugRemove;
import ru.noties.debug.remove.test.BuildConfig;

@DebugRemove(BuildConfig.DEBUG_REMOVE)
public class App extends Application {

//    @Override
//    public void onCreate() {
//        super.onCreate();
//    }

    public static void init(DebugOutput output) {
        Debug.init(output);
    }
}
