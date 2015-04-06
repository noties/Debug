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


### What's new (1.1.3
* Decreased number of method calls inside Debug.java
* Empty Timer implementation if !isDebug


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
![trace](https://raw.githubusercontent.com/noties/Debug/master/pics/trace.png)


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
![someMethod](https://raw.githubusercontent.com/noties/Debug/master/pics/someMethod.png)

![simpleMethod](https://raw.githubusercontent.com/noties/Debug/master/pics/simpleMethod.png)

![onCreate](https://raw.githubusercontent.com/noties/Debug/master/pics/onCreate.png)


* Simple exception handling

```java
try {
    new AssertionError("This is exception");
} catch (Throwable throwable) {
     Debug.e(e);
}
```

Output:
![someMethodWithException](https://raw.githubusercontent.com/noties/Debug/master/pics/someMethodWithException.png)


* Timer with millis & nanos

*see sample application for code*

Output millis:
![timer](https://raw.githubusercontent.com/noties/Debug/master/pics/timer.png)


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
