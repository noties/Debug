package io.noties.debug

// Whoa, https://stackoverflow.com/a/54844203
//  just suppressing it resolves compilation error
@Suppress("NO_ACTUAL_FOR_EXPECT", "FunctionName")
expect fun TimestampISO8601DebugLogInterceptor(iso8601Pattern: String): DebugLogInterceptor