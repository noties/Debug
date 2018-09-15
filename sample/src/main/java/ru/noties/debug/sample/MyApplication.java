package ru.noties.debug.sample;

import android.app.Application;

import ru.noties.debug.AndroidLogDebugOutput;
import ru.noties.debug.Debug;


public class MyApplication extends Application {

//    public void a() {
//Debug.init(new AndroidLogDebugOutput(/*isDebug*/true)); // BuildConfig.DEBUG can be used
//Debug.init(new AndroidLogDebugOutput(true), new SystemOutDebugOutput(true));
//final List<DebugOutput> outputs = /*obtain desired outputs)*/;
//Debug.init(outputs);
//
//Debug.v();
//Debug.d();
//Debug.i();
//Debug.w();
//Debug.e();
//Debug.wtf();
//
//int value = -1;
//try {
//    value = /* obtrain value */;
//    Debug.i("obtained value: %d", value);
//} catch (Throwable throwable) {
//    Debug.e(throwable);
//    Debug.w(throwable, "Exception executing try code block... value: %d");
//}
//    }

    @Override
    public void onCreate() {
        super.onCreate();

        Debug.init(new AndroidLogDebugOutput(BuildConfig.DEBUG));
    }

//    public void someMethod(int i, double d, String s) {
//        Debug.i("i: %s, d: %s, s: %s", i, d, s);
//    }
}
