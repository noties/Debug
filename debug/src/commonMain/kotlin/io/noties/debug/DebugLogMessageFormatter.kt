package io.noties.debug

typealias DebugLogMessageFormatter = (DebugLog) -> String?

// Whoa, https://stackoverflow.com/a/54844203
//  just suppressing it resolves compilation error
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect val DefaultDebugLogMessageFormatter: DebugLogMessageFormatter