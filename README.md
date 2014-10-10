## Debug - Android logging tool

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Debug-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1038)

### Why?
* Lightweight
* Simple
* Powerful

Debug library provides a lot of useful information. For ex., Java file name as tag,
method name and a line number where Debug function was called.

Also, it wraps String.format(), so you can create any message with nearly any quantity of variables to check at almost no pain (and time).

### How to do it

```java
Debug.i();
Debug.v("My message");
Debug.w("x: %d, y: %d, z: %s", 1, 72, .0F);
Debug.d(new Throwable(), "mLastItemIndex: %d", mLastItemIndex);
Debug.e(new Throwable());
```

### Installation
Maven:
```
<dependency>
  <groupId>ru.noties.debug</groupId>
  <artifactId>debug</artifactId>
  <version>1.1.0</version>
  <type>aar</type>
</dependency>
```

Gradle:
```
compile 'ru.noties.debug:1.1.0'
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
10-05 23:57:42.029  13412-13412/ru.noties.debug.sample V/MainActivity.java﹕ trace:
    MainActivity.java : onCreate() : 20
    Activity.java : performCreate() : 5231
    Instrumentation.java : callActivityOnCreate() : 1087
    ActivityThread.java : performLaunchActivity() : 2148
    ActivityThread.java : handleLaunchActivity() : 2233
    ActivityThread.java : access$800() : 135
    ActivityThread.java : handleMessage() : 1196
    Handler.java : dispatchMessage() : 102
    Looper.java : loop() : 136
    ActivityThread.java : main() : 5001
    Method.java : invoke() : -2
    ZygoteInit.java : run() : 785
    ZygoteInit.java : main() : 601
```

* All Android Log levels

```java
Debug.i("x: %d, x2: %d, y: %s", x, x2, y);
Debug.w();
Debug.e("ok");
```

Output:
```
10-05 23:57:42.029  13412-13412/ru.noties.debug.sample I/MainActivity.java﹕ someMethod() : 36 : x: 1, x2: 5, y: Hello!
```

```
10-05 23:57:42.029  13412-13412/ru.noties.debug.sample W/MainActivity.java﹕ simpleMethod() : 48 :
```

```
10-05 23:57:42.029  13412-13412/ru.noties.debug.sample E/MainActivity.java﹕ onCreate() : 30 : ok
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
10-05 23:57:42.029  13412-13412/ru.noties.debug.sample E/MainActivity.java﹕ someMethodWithException() : 43 : Exception: java.lang.AssertionError: This is exception
    java.lang.AssertionError: This is exception
            at ru.noties.debug.sample.MainActivity.someMethodWithException(MainActivity.java:41)
            at ru.noties.debug.sample.MainActivity.onCreate(MainActivity.java:24)
            at android.app.Activity.performCreate(Activity.java:5231)
            at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1087)
            at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2148)
            at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:2233)
            at android.app.ActivityThread.access$800(ActivityThread.java:135)
            at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1196)
            at android.os.Handler.dispatchMessage(Handler.java:102)
            at android.os.Looper.loop(Looper.java:136)
            at android.app.ActivityThread.main(ActivityThread.java:5001)
            at java.lang.reflect.Method.invoke(Native Method)
            at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:785)
            at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:601)
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