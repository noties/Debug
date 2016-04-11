# The problem

There are a lot of logging tools to make Android developer happy: http://android-arsenal.com/tag/57 to name a few (and of cause my humble contribution: https://github.com/noties/Debug).

They all provide a nice range of features to log and/or debug running Android application. But all of them have a major drawback - **each value passed to log call is computed even though it may not be printed**.

(For my own comfort I'll be using my `Debug` library in examples, but it can be easily replaced with any other library) Suggest this:

```java
Debug.i("computedValue: %s", compute());
```

Here's what going on here: we call logging method passing a pattern and a value to be printed. If we configured `Debug` to print debug messages (`Debug.init(new AndroidDebugOutput(true))`) everything works as expected - the value is computed and then printed to the logcat (with a nice feature to navigate to the calling code line in case of `Debug`). But if we configured library to be in release mode (`Debug.init(new AndroidDebugOuput(false))`) we have a problem: the value is still computed even though it's not printed. D'oh!

Maybe when debugging it's not that big issue, but for release builds it's certainly not desired. 

# Seeking solution

I've been thinking a lot about this problem, but every solution that I came up was not satisfying. 


### Lazy

Wrap computed values in something like `Lazy` and add support for it into the library. 
```java
interface Provider<T> {
    T provide();
}

class Lazy<T> {

    final Provider<T> provider;
    T value;
    volatile boolean isProviderCalled;

    Lazy(Provider<T> provider) {
        this.provider = provider;
    }
    T get() {
        if (!isProviderCalled) {
            synchronized(this) {
                if (!isProviderCalled) {
                    value = provider.provide();
                    isProviderCalled = true;
                }
            }
        }
        return value;
    }
}
```
```java
Debug.i("computedValue: %s", new Lazy<>(new Provider<Object>() {
    @Override
    public Object provide() {
        return compute();
    }
}));
```

I thought that it's too verbose and still there are new objects allocations. Of cause, I could skip the support in `Debug` library by overriding `toString()` method in `Lazy` class, but it doesn't matter much at long as logging would be such a verbose monstrous piece of code. Another option is to pass to `Debug` `Lazy<String[]>` as a parameter - this way we could pass all parameters in one object, but it still needs support from the library and it's still too verbose.


### Anonymous objects 

OK, if we can override `toString` method in any object, why bother with library specific API? We can do it with simple anonymous objects:
```java
Debug.i("computedValue: %s", new Object(){public String toString() {return "" + compite();}});
```
But it has the same drawbacks as `lazy` solution, it's still monstrously verbose.


### String templates

Another thought was to create string templates for Java, something like: `"computedValue: ${compute()}"`.  It seems much better, but it would be definitely a very compelling task. The only possible solution that I see to achieve this functionality is so modify Java syntax tree. But we don't want to traverse all the `String`'s in Java code to check if they contain templates (after all it's one of the basic types in Java). Thus we have to provide our syntax-tree-modifier a context in which string template is used. It would be easy if we could do something like this:
```java
Debug.i(@Template "computedValue: ${compute}");
```
or this:
```java
@Template
Debug.i("computedValue: ${compute}");
```
But in Java we cannot annotate such elements. We can, though, annotate local variables:
```java
@Template
final String template = "computedValue: ${compute}";
Debug.i(template);
```
OK, it's not that beautiful, but at least we have our context now. Next we have to implement own small Java source code parser to parse templates and convert them to a valid Java syntax tree. Then we have to modify existing syntax tree & pray that compilation will not fail afterwards.

Although it seems like a very interesting task for me, I don't think that it's a good solution to the problem of debug/release logging. 


### Debug code block

After some time the ideal solution began to take a shape of something like this:
```java
@Debug
{
    Debug.i("computedValue: %s", compute());
}
```
The standalone code block has a lot of niceties. It has it's own scope, so we can execute a lot of arbitrary code inside it and no one outside of the scope will see it. It's also quite visible whilst reading. But unfortunately we cannot annotate code blocks in Java.

# The solution

After some days I remembered a Java construct that (I hope) is hardly ever used nowadays - **label**. And everything clicked.

 * **Labels** are build-in Java
 * **Labels** are easily identified whilst traversing Java syntax tree
 * **Labels** are readable
 * **Labels** are hardly ever used

```java
debug: Debug.i("computedValue: %s", compute());
debug: {
    final int computedValue = compute();
    Debug.i("computedValue: %d", computedValue);
}
```
And this solution is applicable for all logging tools, not only my beloved `Debug`. More than that it's applicable for everything Java-valid! I began coding and almost immediately the first version was ready. It took literally few lines of code.

I created a simple annotation `DebugConfiguration` that takes:

 * `allLabels` - all labels that should be processed
 * `enabledLabels` -  labels that enabled for this build

When annotation processor tool will be launched, it will extract difference from these two and just remove all the labels that are in `allLabels`, but not in `enabledLabels`. 

It would be logical to make these two parameters arrays of `String`, but this will tight developers hands a bit, as long as Java annotations can take only compile time constants. `String[]` for example generated in Gradle build script and placed in `BuildConfig` file cannot be passed to any annotation. Because it's mutable and thus not a constant (consider this: `BuildConfig.SOME_ARRAY[0] = null;`). That's why I decided to make these parameters simple `String`'s concated with `|` delimiter. That may seem as an extra work, but concating strings like this is a simple task for Gradle. 


# How-to

[![Maven Central](https://img.shields.io/maven-central/v/ru.noties/debug-compiler.svg)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%22debug-compiler%22) [![Maven Central](https://img.shields.io/maven-central/v/ru.noties/debug-annotations.svg)](http://search.maven.org/#search|ga|1|g%3A%22ru.noties%22%20AND%20a%3A%22debug-annotations%22)

Add this two lines to your `dependencies` block in `build.gradle`:
```groovy
apt 'ru.noties:debug-compiler:1.0.1'
provided 'ru.noties:debug-annotations:1.0.1'
```

Prepare two required values for this library:
```groovy
defaultConfig {
    // if you wish to provide multiple values
    // use `|` as a delimiter, for example:
    // \"debug|release|debug|release\"
    buildConfigField 'String', 'ALL_LABELS', '\"debug\"'
}
buildTypes {
    release {
        buildConfigField 'String', 'ENABLED_LABELS', '\"\"'
    }
    debug {
        buildConfigField 'String', 'ENABLED_LABELS', '\"debug\"'
    }
}
```

Annotate one of the project's classes with`@DebugConfiguration` (`Application` class is a common pattern). But it could be any class in your project.
```java
@DebugConfiguration(
    allLabels = BuildConfig.ALL_LABELS,
    enabledLabels = BuildConfig.ENABLED_LABELS
)
public class MyApplication extends Application {}
```

Now you are good to go.
```java
debug: Debug.i("computedValue: %s", compute());
debug: {
    Debug.i("computedValue: %s", compute());
}
```

Of cause I'm using `debug` label name as an example, you can use whatever name as you wish - `d`, `i`, `whatever`, etc.


# What's next?
This solution for zero penalty performance logging for release builds goes a bit further. Well, quite, actually. For example, we have 4 different types of functionality combined in different product flavors of our application:

 * `log` for logging in debug builds
 * `track` for statistics in release builds
 * `free` for functionality in free version of an application
 * `paid` for functionality in paid version

```groovy
final def debugDatas = new HashMap()

android {

    /** data omitted **/

    defaultConfig {

        /** data omitted **/
	
        buildConfigField 'String', 'ALL_LABELS', '\"log|track|free|paid\"'
    }
    
    buildTypes {
        release { /** data omitted **/ }
        debug { /** data omitted **/ }
    }

    productFlavors {
        free {
            debugDatas.put(name, new DebugData(track: true, free: true, paid: false))
        }

        paid {
            debugDatas.put(name, new DebugData(track: false, free: false, paid: true))
        }
    }
}

android.applicationVariants.all { variant ->
    final String name = variant.name
    final def debug = name.endsWith('Debug')
    final String flavor = debug ? name.substring(0, name.lastIndexOf('Debug')) : name.substring(0, name.lastIndexOf('Release'))
    final def data = debugDatas.get(flavor)
    variant.buildConfigField 'String', 'ENABLED_LABELS', "\"${data.value(debug)}\""
}

dependencies {
    compile 'ru.noties:debug:2.0.2@jar'

    apt 'ru.noties:debug-compiler:1.0.1'
    provided 'ru.noties:debug-annotations:1.0.1'
}

public class DebugData {
    def track
    def free
    def paid
    public def value(def log) {
        ["track": track, "free": free, "paid": paid, "log": log]
                .grep { e -> e.value }
                .flatten { e -> e.key }
                .join('|')
    }
}
```

And in Java code:

```java
void someMethod() {
    log: Debug.i("value: %d", value());
    free: {
        log: Debug.i("heavyComputation: %s", heavyComputation());
        track: mStats.track("`someMethod` visited")
    }
    paid: {
        log: Debug.i("anotherHeavyComputation: %s", anotherHeavyComputation());
        track: mStats.track("`someMethod` visited with value: %s", someValue())
    }
}
```
As you can see, nested labels are OK.

# More info
* http://scg.unibe.ch/archive/projects/Erni08b.pdf

## License

```
  Copyright 2015 Dimitry Ivanov (mail@dimitryivanov.ru)

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
```
