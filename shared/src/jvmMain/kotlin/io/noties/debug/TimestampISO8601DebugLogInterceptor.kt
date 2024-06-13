package io.noties.debug

@Suppress("FunctionName")
actual fun TimestampISO8601DebugLogInterceptor(iso8601Pattern: String): DebugLogInterceptor {
    return JavaTimeDebugLogInterceptor(iso8601Pattern)
}