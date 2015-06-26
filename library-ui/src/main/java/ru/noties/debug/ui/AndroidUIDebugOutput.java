package ru.noties.debug.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import ru.noties.debug.Level;
import ru.noties.debug.out.DebugOutput;
import ru.noties.debug.ui.model.LogDatabase;
import ru.noties.debug.ui.model.LogItem;
import ru.noties.debug.ui.model.LogModel;

/**
 * Created by Dimitry Ivanov on 25.06.2015.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class AndroidUIDebugOutput implements DebugOutput, Application.ActivityLifecycleCallbacks {

    public static final String TAG_LOG = "tag.ru.noties.Debug.ui.Log";

    private final boolean isDebug;
    private final Context appContext;

    private WeakReference<Activity> mCurrentActivity;
    private View mButton;

    public AndroidUIDebugOutput(Application application, boolean isDebug) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            if (isDebug) {
                throw new IllegalStateException("AndroidUIDebugOutput could be used only with sdk version >= 14");
            } else {
                this.isDebug = false;
                this.appContext = null;
            }
        } else {

            this.appContext = application.getApplicationContext();

            if (isDebug) {
                application.registerActivityLifecycleCallbacks(this);
                this.isDebug = true;
                LogDatabase.getInstance().init(appContext);
            } else {
                this.isDebug = false;
            }
        }
    }

    @Override
    public void log(Level level, Throwable throwable, String tag, String message) {
        // check for current activity
        // check for current View
        final LogItem item = new LogItem()
                .setDate(System.currentTimeMillis())
                .setLevel(level)
                .setTag(tag)
                .setMessage(message);
        new LogModel(LogDatabase.getInstance()).save(item);
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
        mCurrentActivity = new WeakReference<Activity>(activity);
        final ViewGroup content = (ViewGroup) activity.findViewById(getViewGroupIdToAttachTo());
        if (content == null) {
            // nothing we can do
            return;
        }
        if (mButton == null) {
            mButton = getButtonView(content);
        }
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick();
            }
        });
        content.addView(mButton);
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (mButton != null
                && mButton.getParent() != null) {
            ((ViewGroup) mButton.getParent()).removeView(mButton);
            mButton = null;
        }
        mCurrentActivity = null;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    protected Activity getCurrentActivity() {
        if (mCurrentActivity != null) {
            return mCurrentActivity.get();
        }
        return null;
    }

    protected int getViewGroupIdToAttachTo() {
        return android.R.id.content;
    }

    protected View getButtonView(ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(appContext);
        return inflater.inflate(R.layout.view_log_button, parent, false);
    }

    protected void onButtonClick() {
        final Activity activity = getCurrentActivity();
        if (activity == null) {
            return;
        }
        final boolean isSupport = activity instanceof FragmentActivity;

        if ((isSupport && hideFragmentCompat((FragmentActivity) activity))
                || hideFragment(activity)) {
            return;
        }

        final UIFragmentView fragmentView = new UIFragmentView();

        if (isSupport) {
            showFragmentCompat((FragmentActivity) activity, fragmentView);
        } else {
            showFragment(activity, fragmentView);
        }
    }

    private void showFragmentCompat(FragmentActivity activity, UIFragmentView view) {
        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(getViewGroupIdToAttachTo(), getCompatFragment(view), TAG_LOG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    private void showFragment(Activity activity, UIFragmentView view) {
        activity.getFragmentManager()
                .beginTransaction()
                .add(getViewGroupIdToAttachTo(), getNormalFragment(view), TAG_LOG)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    private boolean hideFragmentCompat(FragmentActivity activity) {
        final Fragment fragment = activity.getSupportFragmentManager().findFragmentByTag(TAG_LOG);
        if (fragment == null) {
            return false;
        }
        activity.getSupportFragmentManager().popBackStack();
//        activity.getSupportFragmentManager()
//                .beginTransaction()
//                .detach(fragment)
//                .commitAllowingStateLoss();
        return true;
    }

    private boolean hideFragment(Activity activity) {
        final android.app.Fragment fragment = activity.getFragmentManager().findFragmentByTag(TAG_LOG);
        if (fragment == null) {
            return false;
        }
//        activity.getFragmentManager()
//                .beginTransaction()
//                .detach(fragment)
//                .commitAllowingStateLoss();
        activity.getFragmentManager().popBackStack();
        return true;
    }

    protected Fragment getCompatFragment(UIFragmentView view) {
        return UIFragmentCompat.newInstance(view);
    }

    protected android.app.Fragment getNormalFragment(UIFragmentView view) {
        return UIFragment.newInstance(view);
    }

    protected Context getAppContext() {
        return appContext;
    }
}
