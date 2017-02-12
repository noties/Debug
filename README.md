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

Note that if you are using this library in multi-process application, you must call `Debug.init(boolean)` for every process.


Also, it wraps String.format(), so you can create any message with nearly any
quantity of variables to check at almost no pain (and time).

## Android Metaprogramming with `debug-compiler` & `debug-annotations`
I've added a simple module to actually remove all the logging calls from the source code.
I was not satisfied that all the logging was still present in release builds. Yes, the output was clean,
but all the computations were there. For example:
```java
Debug.i("someValue1: %d, someValue2: %s, equals: %s", compute1(), compute2(), compute1().equals(compute2());
```
Will compute anyway all the values passed to the `i` call, but won't print them in release builds.
Yes, logcat is clean, but we have computed these values, so we cannot say that this logging is completely harmless
for application's performance. Please refer to the `README.md` in the `apt-compiler` module
to get more information on how to achieve zero performance penalty for specific builds.


## What's new (2.0.2)
Added `jar` artifact for `lib` module.
```gradle
compile 'ru.noties:debug:2.0.2@jar'
```

### What's new (2.0.0)
Added a concept of `DebugOutput`. Now different *out* policies can be configured. For example, if you wish to write debug logs not only to logcat, but also to a file or send to a server or whatever you wish to do with data, it now could be easily done. I bundled library with these outs (`ru.noties.debug.out`):
* `AndroidLogDebugOutput` - simple logcat
* `FileDebugOutput` - writing to a file
* `UncaughtExceptionDebugOutput` - logging of uncaught exceptions (which would be passed to `Debug.e()`)
* `DebugOutputFacade` - to wrap more than one `DebugOutput`

Of cause, you could easily create your own output - just implement `ru.noties.debug.DebugOutput` and pass it to `Debug` while initialization.


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
`Debug.init()` method takes one `DebugOutput` as a parameter.

```java
public class DebugApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Debug.init(new AndroidLogDebugOutput(BuildConfig.DEBUG));
    }
}
```

If you wish to customize the output policy it could be easily done with `DebugOutputFacade`

```java
final boolean isDebug = BuildConfig.DEBUG;
final DebugOutputFacade output = DebugOutputFacade.newInstance(
    new AndroidLogDebugOutput(isDebug),
    new UncaughtExceptionDebugOutput(isDebug),
    ...
);
Debug.init(output);
```

#### DebugOutput
Common interface to configure Debug output
```java
public interface DebugOutput {
    void log(Level level, Throwable throwable, String tag, String message);
    boolean isDebug();
}
```

#### UncaughtExceptionDebugOutput
Logs uncaught exceptions (aka `Force close`) and passes it to `Debug.e()`

#### FileDebugOutput
Could be used to write Debug logs to a file. Should be obtained via static methods `newInstance`
```java
public static FileDebugOutput newInstance(
        boolean isDebug,
        boolean isAsync,
        FileStrategy fileStrategy
) throws UnableToObtainFileException { ... }
```
or
```java
public static FileDebugOutput newInstance(
        boolean isDebug,
        boolean isAsync,
        FileStrategy fileStrategy,
        OutputConverter outputConverter
) throws UnableToObtainFileException { ... }
```
`isDebug` - indicates whether DebugOutput is active and should write logs

`isAsync` - indicates whether writing should be done in a background thread (since it's IO)

**FileStrategy**
```java
public interface FileStrategy {
    File newSession() throws UnableToObtainFileException;
}
```
If no major customization is needed `SimpleFileStrategy` could be used.
```java
public static SimpleFileStrategy newInstance(
        File folder,
        String logFolderName
) throws InitializationException { ... }
```
or
```java
public static SimpleFileStrategy newInstance(
        File folder,
        String logFolderName,
        LogFileNameStrategy logFileNameStrategy
) throws InitializationException { ... }
```

**OutputConverter**

Used to convert log message into String
```java
public interface OutputConverter {
    String convert(Level level, Throwable throwable, String tag, String message);
}
```

So, the initialization of `FileDebugOutput` could look like:
```java
private static DebugOutput getFileOutput(Context appContext, boolean isDebug) {
    try {
        return FileDebugOutput.newInstance(
            isDebug,
            true,
            SimpleFileStrategy.newInstance(appContext.getExternalCacheDir(), "debug_logs")
        );
    } catch (FileDebugOutput.UnableToObtainFileException e) {
        e.printStackTrace(); // Debug library is not initialized yet, we could not pass this throwable to it
    } catch (SimpleFileStrategy.InitializationException e) {
        e.printStackTrace();
    }
    return null;
}
```

## Debug-UI
[![Maven Central](https://img.shields.io/maven-central/v/ru.noties/debug-ui.svg)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%22debug-ui%22)
```groovy
compile 'ru.noties:debug-ui:x.x.x'
compile 'ru.noties:storm:1.0.3' // required dependancy for writing logs to SQLite database
```
Debug-UI is a standalone library for visualizing Debug logs at your device for SDK_INT >= 14.

![debug-ui](https://raw.githubusercontent.com/noties/Debug/master/pics/debug-ui.png)
![debug-ui-expand](https://raw.githubusercontent.com/noties/Debug/master/pics/debug-ui-expand.png)

```java
final DebugOutput uiOutput = new AndroidUIDebugOutput(
    mApplication, // Application
    BuildConfig.DEBUG
);
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
