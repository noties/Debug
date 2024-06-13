package io.noties.debug.outputs

import android.util.Log
import io.noties.debug.DebugLogLevel
import io.noties.debug.DebugOutput

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

        Log.println(logLevel, logTag, logMessage)
    }
}