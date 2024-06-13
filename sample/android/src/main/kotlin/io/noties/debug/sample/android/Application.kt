package io.noties.debug.sample.android

import android.app.Application
import io.noties.debug.Debug
import io.noties.debug.DefaultDebugLogMessageFormatter
import io.noties.debug.JavaTimeDebugLogInterceptor
import io.noties.debug.PrependCallStack

class Application : Application() {

    override fun onCreate() {
        super.onCreate()

        Debug.configure {
            android

            // multiple of the same type is available, 2 android outputs would be added
            android
                // creates different log string
                .messageFormatter { log ->
                    log.toString()
                }

            // another android output, but it is disabled and would never receive an input
            android.disabled()

            // other platforms are available too, but they no-op if not target,
            //  here, `iOS` is not available on android (obviously)
            iOS

            @OptIn(PrependCallStack::class)
            java
                .interceptor {
                    this.prependCallStack()
                        .timestamp("yyyy-MM-dd HH:mm:ss.SSS VV")
                }

            // by default no output creates a timestamp, but this could be configured with
            //  one of the interceptors
            kotlin
                .interceptor {
                    this
                        .add(JavaTimeDebugLogInterceptor("yyyy-MM-dd HH:mm:ss.SSS VV"))
                        // or:
//                        .add(JavaCalendarDebugLogInterceptor("yyyy-MM-dd HH:mm:ss.SSS z"))
                }
                // process output message
                .messageFormatter {
                    // for example, wrap strings in quotes (process message directly)
                    val message = it.message.map { (it as? CharSequence)?.let { "\"$it\"" } ?: it }
                    // and pass it to default formatter
                    DefaultDebugLogMessageFormatter(it.copy(message = message))
                }
        }

        // log to the root instance
        Debug.v("Hello", 42)

        // Create a tag and log there.
        // Tags can be nested, in which case
        //  they would be joined with a slash - `some-tag/another-tag`
        Debug.tag("some-tag")
            .v("World", null, 180)

        // exceptions can be passed at any positions
        Debug.i(
            "Exceptions...",
            IllegalStateException("The first"),
            "then some other arguments",
            871.99F,
            true,
            IllegalArgumentException("The last, but not least")
        )
    }
}
