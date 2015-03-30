## Debug - Android logging tool

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Debug-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1038)

### Why?
* Lightweight
* Simple
* Powerful


Debug library provides a lot of useful information. For ex., Java file name as a tag,
method name and a line number where Debug function was called.
Library provides the ability to jump to the source right from the logcat output.


It has no impact on release code - no additional information will be collected at runtime (aka stacktrace).
The only thing is no do is to send a bool to the Debug.init() method. If you are using 
Gradle - it just simplifies the initialization to a line of code:
```java
Debug.init(BuildConfig.DEBUG);
```
Well, if you are not using Gradle, where surely **must** be a way...

Note that if you are using this library in multi-process application, you must call `Debug.init(boolean)` for every process.


Also, it wraps String.format(), so you can create any message with nearly any 
quantity of variables to check at almost no pain (and time).


### What's new (1.1.2)
* Methods in logcat are now clickable
* Small change to trace pattern to exclude Debug.java

### How to do it

```java
Debug.i();
Debug.v("My message");
Debug.w("x: %d, y: %d, z: %s", 1, 72, .0F);
Debug.d(new Throwable(), "mLastItemIndex: %d", mLastItemIndex);
Debug.e(new Throwable());
```

### Installation

[![Maven Central](https://img.shields.io/maven-central/v/ru.noties/debug.svg)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%22debug%22)

Gradle:
```groovy
compile 'ru.noties:debug:x.x.x'
```

The best place to initialise Debug library is an Application's onCreate() method.
Debug.init() takes a boolean indicating whether logging should be done or skipped.

```java
public class DebugApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Debug.init(BuildConfig.DEBUG);
    }
}
```

### Usage

* Trace method calls till current method
```java
Debug.trace();
```

Output:
```
03-23 23:48:59.988  10335-10335/ru.noties.debug.sample V/MainActivity.java﹕ trace:
            at ru.noties.debug.sample.MainActivity.onCreate(MainActivity.java:20)
            at android.app.Activity.performCreate(Activity.java:5990)
            at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1106)
            at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2278)
            at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:2387)
            at android.app.ActivityThread.access$800(ActivityThread.java:151)
            at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1303)
            at android.os.Handler.dispatchMessage(Handler.java:102)
            at android.os.Looper.loop(Looper.java:135)
            at android.app.ActivityThread.main(ActivityThread.java:5254)
            at java.lang.reflect.Method.invoke(Method.java:-2)
            at java.lang.reflect.Method.invoke(Method.java:372)
            at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:903)
            at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:698)
```

* All Android Log levels

```java
Debug.v();
Debug.d();
Debug.i();
Debug.w();
Debug.e();
Debug.wtf();
```

Output:
```
03-23 23:48:59.988  10335-10335/ru.noties.debug.sample I/MainActivity.java﹕ someMethod(MainActivity.java:36) : x: 1, x2: 5, y: Hello!
```

```
03-23 23:48:59.990  10335-10335/ru.noties.debug.sample W/MainActivity.java﹕ simpleMethod(MainActivity.java:48) :
```

```
03-23 23:48:59.991  10335-10335/ru.noties.debug.sample E/MainActivity.java﹕ onCreate(MainActivity.java:30) : ok
```

* Simple exception handling

```java
try {
    new AssertionError("This is exception");
} catch (Throwable throwable) {
     Debug.e(e);
}
```

Output:

```
03-23 23:48:59.989  10335-10335/ru.noties.debug.sample E/MainActivity.java﹕ someMethodWithException(MainActivity.java:43) : Exception: java.lang.AssertionError: This is exception
    java.lang.AssertionError: This is exception
            at ru.noties.debug.sample.MainActivity.someMethodWithException(MainActivity.java:41)
            at ru.noties.debug.sample.MainActivity.onCreate(MainActivity.java:24)
            at android.app.Activity.performCreate(Activity.java:5990)
            at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1106)
            at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2278)
            at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:2387)
            at android.app.ActivityThread.access$800(ActivityThread.java:151)
            at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1303)
            at android.os.Handler.dispatchMessage(Handler.java:102)
            at android.os.Looper.loop(Looper.java:135)
            at android.app.ActivityThread.main(ActivityThread.java:5254)
            at java.lang.reflect.Method.invoke(Native Method)
            at java.lang.reflect.Method.invoke(Method.java:372)
            at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:903)
            at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:698)
```

* Timer with millis & nanos

*see sample application for code*

Output millis:
```
10-05 23:57:42.919  13412-13436/ru.noties.debug.sample I/MainActivity.java﹕ run() : 83 : timer
    Timer #1
    start, 0 ms, here we go, someVar: 10
    + 0 ms,
    + 65 ms, message, i: 1
    + 5 ms,
    + 51 ms, message, i: 3
    + 3 ms,
    + 18 ms, message, i: 5
    + 95 ms,
    + 23 ms, message, i: 7
    + 63 ms,
    + 74 ms, message, i: 9
    + 14 ms,
    + 38 ms, message, i: 11
    + 33 ms,
    + 92 ms, message, i: 13
    + 28 ms,
    + 17 ms, message, i: 15
    + 31 ms,
    + 60 ms, message, i: 17
    + 71 ms,
    + 40 ms, message, i: 19
    + 7 ms,
    + 37 ms, message, i: 21
    stop, 22 ms,
    took: 887 ms
```


### ~~What's next?~~ @Deprecated - Do not use this, as long as you can lose your own live templates

If you are using Intellij Idea or Android Studio, you can make debugging even simpler.
I wrote some Live Templates for this lib.
Download *ru.noties.debug.live_templates.jar* and import it in your IDE. After that you could do something like that:

```
    di
    de
    dw
    dd
    dv
```

and hit tab (once at a time of cause), you will get:

```java
    Debug.i("", );
    Debug.e("", );
    Debug.w("", );
    Debug.d("", );
    Debug.v("", );
```


also, there are **dii**, **dee**, **dww**, **ddd** and **dvv**. After typing them you will get:

```java
    Debug.e("view: %s, firstVisibleItem: %s, visibleItemCount: %s, totalItemCount: %s", view, firstVisibleItem, visibleItemCount, totalItemCount);
    Debug.i("view: %s, firstVisibleItem: %s, visibleItemCount: %s, totalItemCount: %s", view, firstVisibleItem, visibleItemCount, totalItemCount);
    Debug.v("view: %s, firstVisibleItem: %s, visibleItemCount: %s, totalItemCount: %s", view, firstVisibleItem, visibleItemCount, totalItemCount);
    Debug.d("view: %s, firstVisibleItem: %s, visibleItemCount: %s, totalItemCount: %s", view, firstVisibleItem, visibleItemCount, totalItemCount);
    Debug.w("view: %s, firstVisibleItem: %s, visibleItemCount: %s, totalItemCount: %s", view, firstVisibleItem, visibleItemCount, totalItemCount);
```

for a OnScrollListener.onScroll() method. Yes, the pattern will be created from method's parameters and you will not have to write it yourself.
