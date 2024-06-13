package io.noties.debug

import android.os.Build

@Suppress("FunctionName")
actual fun TimestampISO8601DebugLogInterceptor(iso8601Pattern: String): DebugLogInterceptor {
    return factory(iso8601Pattern)
}

private val factory: (String) -> DebugLogInterceptor =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        { JavaTimeDebugLogInterceptor(it) }
    } else {
        { JavaCalendarDebugLogInterceptor(it) }
    }