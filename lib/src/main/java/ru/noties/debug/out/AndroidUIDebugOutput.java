package ru.noties.debug.out;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import ru.noties.debug.Level;

/**
 * Created by Dimitry Ivanov on 25.06.2015.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class AndroidUIDebugOutput implements DebugOutput, Application.ActivityLifecycleCallbacks {

    private final boolean isDebug;

    public AndroidUIDebugOutput(Application application, boolean isDebug) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            throw new IllegalStateException("AndroidUIDebugOutput could be used only with sdk version >= 14");
        }

        if (isDebug) {
            application.registerActivityLifecycleCallbacks(this);
            this.isDebug = true;
        } else {
            this.isDebug = false;
        }
    }

    @Override
    public void log(Level level, Throwable throwable, String tag, String message) {
        // check for current activity
        // check for current View
    }

    @Override
    public boolean isDebug() {
        return isDebug;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    protected int getViewGroupIdToAttachTo() {
        return android.R.id.content;
    }
}
