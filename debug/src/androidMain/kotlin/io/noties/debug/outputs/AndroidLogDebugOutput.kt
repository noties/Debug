package io.noties.debug.outputs

import android.util.Log
import io.noties.debug.DebugLogLevel
import io.noties.debug.DebugOutput

private const val MAX_LINE_LENGTH = 4000

actual val AndroidLogDebugOutput: DebugOutput = { formatter, log ->
    val level = log.level
    if (level != null) {
        val logLevel = when (log.level) {
            DebugLogLevel.VERBOSE -> Log.VERBOSE
            DebugLogLevel.DEBUG -> Log.DEBUG
            DebugLogLevel.INFO -> Log.INFO
            DebugLogLevel.WARN -> Log.WARN
            DebugLogLevel.ERROR -> Log.ERROR
        }
        val logTag = log.tag ?: ""
        val logMessage = formatter(log.copy(timestamp = null, tag = null, level = null)) ?: ""
        val logLines = logMessage.windowed(
            MAX_LINE_LENGTH,
            MAX_LINE_LENGTH,
            partialWindows = true
        )

        for (line in logLines) {
            Log.println(logLevel, logTag, line)
        }
    }
}