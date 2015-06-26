package ru.noties.debug.sample;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.noties.debug.Debug;
import ru.noties.debug.out.AndroidLogDebugOutput;
import ru.noties.debug.out.DebugOutput;
import ru.noties.debug.out.DebugOutputFacade;
import ru.noties.debug.out.FileDebugOutput;
import ru.noties.debug.out.SimpleFileStrategy;
import ru.noties.debug.out.UncaughtExceptionDebugOutput;
import ru.noties.debug.ui.AndroidUIDebugOutput;

/**
 * Created by dimaster on 05.10.14.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final boolean isDebug = BuildConfig.DEBUG;

        final List<DebugOutput> debugOutputs = new ArrayList<>();
        debugOutputs.add(new AndroidLogDebugOutput(isDebug));
        debugOutputs.add(new AndroidUIDebugOutput(this, isDebug));
        debugOutputs.add(new UncaughtExceptionDebugOutput());
        final DebugOutput fileOutput = getFileOutput(getApplicationContext(), isDebug);
        if (fileOutput != null) {
            debugOutputs.add(fileOutput);
        }

        Debug.init(DebugOutputFacade.newInstance(debugOutputs));
    }

    private static DebugOutput getFileOutput(Context appContext, boolean isDebug) {
        //noinspection TryWithIdenticalCatches
        try {
            return FileDebugOutput.newInstance(isDebug, true, SimpleFileStrategy.newInstance(appContext.getExternalCacheDir(), "debug_logs", new SimpleFileStrategy.LogFileNameStrategy() {

                private static final String PATTERN = "%d_%s";

                @Override
                public String create() {
                    return String.format(PATTERN, System.currentTimeMillis(), new Date());
                }
            }));
        } catch (FileDebugOutput.UnableToObtainFileException e) {
            e.printStackTrace();
        } catch (SimpleFileStrategy.InitializationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
