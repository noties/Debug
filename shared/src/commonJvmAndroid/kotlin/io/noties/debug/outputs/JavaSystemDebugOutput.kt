package io.noties.debug.outputs

import io.noties.debug.DebugLogLevel
import io.noties.debug.DebugOutput

actual val JavaSystemDebugOutput: DebugOutput = { formatter, log ->
    val text = formatter(log)
    val out = if (log.level == DebugLogLevel.ERROR) {
        System.err
    } else {
        System.out
    }
    out.println(text)
}